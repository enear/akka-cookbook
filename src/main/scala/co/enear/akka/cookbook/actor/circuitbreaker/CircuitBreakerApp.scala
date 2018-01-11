package co.enear.akka.cookbook.actor.circuitbreaker

import akka.actor.ActorSystem
import akka.pattern.CircuitBreaker

import scala.concurrent.duration.{Duration, _}
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}

object CircuitBreakerApp extends App {
  val system = ActorSystem("circuit-breaker")

  /**
    * Obtain implicitly the akka ExecutionContext.
    */
  import system.dispatcher

  /**
    * A circuit breaker guards a computation from repeated failure.
    * It opens when failures are over maxFailures and does not call
    * the guarded function from now on, failing fast.
    * It retries the guarded function every resetTimeout when the circuit is open.
    * If the retry succeeds, we close the circuit breaker and the guarded function
    * is called again.
    * callTimeout is an upper time bound on the guarded function before signaling failure.
    */
  val breaker = CircuitBreaker(
    system.scheduler,
    maxFailures = 1,
    callTimeout = 10.second,
    resetTimeout = 1.seconds
  )

  val failure = Future.failed(new Exception("..."))
  val success = Future.successful()
  val computations = (
    Stream.fill(10)(success) ++
    Stream.fill(2)(failure) ++
    Stream.fill(20)(success)
    ).map(f => breaker.withCircuitBreaker(f))
    .map { f =>
      Thread.sleep(200) // delaying the stream a bit
      f
    }

  /**
    *
    * Bonus: Scheduler allows to run computations from time to time.
    */
  system.scheduler.scheduleOnce(delay = 1.second) {
    system.log.info("Starting a bunch of computations in a circuit breaker.")
    computations.foreach(_.onComplete {
      case Success(x) => system.log.info(x.toString)
      case Failure(x) => system.log.info(x.toString)
    })
  }

  Thread.sleep(10000)

  Await.result(system.terminate(), Duration.Inf)
}


