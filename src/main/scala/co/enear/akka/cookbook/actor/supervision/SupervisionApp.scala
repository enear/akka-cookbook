package co.enear.akka.cookbook.actor.supervision

import akka.actor.{ActorSystem, Props}
import akka.pattern.BackoffSupervisor

import scala.concurrent.Await
import scala.concurrent.duration._

object SupervisionApp extends App {
  val system = ActorSystem("supervision")

  val parent = system.actorOf(Props[Papa])
  parent ! SweetChildOfMine

  val backoffSupervisor = BackoffSupervisor.props(
    Props[Papa],
    "papa",
    5.seconds,
    30.seconds,
    0.5
  )

  Thread.sleep(5000)

  Await.result(system.terminate(), Duration.Inf)
}
