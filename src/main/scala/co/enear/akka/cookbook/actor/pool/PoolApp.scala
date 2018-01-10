package co.enear.akka.cookbook.actor.pool

import akka.actor.{ActorSystem, Props}
import akka.routing.{Broadcast, DefaultResizer, SmallestMailboxPool}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * A pool of actors allows routing messages to the actors in it, or in other words,
  * allows abstracting the way messages are split between them.
  *
  * Implementations of actor pools:
  *
  * BalancingPool redistributes work amongst actors, while using a single mailbox.
  * RoundRobinPool sends messages to actors in a loop.
  * BroadcastPool sends the same message to all actors in the pool.
  * ScatterGatherFirstCompletedPool sends the same message to all actors and
  * gathers the first completed response (using ask).
  * TailChoppingPool works like a random round robin, and keeps only the first
  * of the replies.
  * ConsistentHashingPool is used to send a message hashed with key. The key is used to
  * retrieve some cached computation value from the pool. A message can
  * extend ConsistentHashable message in order to be processed by the pool.
  * RandomPool sends messages to a random actor in the pool.
  */
object PoolApp extends App {
  val system = ActorSystem("pool")

  /**
    * A resizer such as the DefaultResizer allows
    * resizing the pool according to pressure.
    */
  val resizer = DefaultResizer(lowerBound = 2, upperBound = 10)

  /**
    * Create a pool of actors which sends messages
    * to actors with least filled mailbox.
    */
  val pool = system.actorOf(
    SmallestMailboxPool(
      nrOfInstances = 10,
      resizer = Some(resizer)
    ).props(Props[PoolBoy]))

  /**
    * These messages will be handled by the actor with the smallest mailbox.
    */
  for (_ <- 1 to 20) {
    pool ! SequenceMessage
  }

  /**
    * Send a message to all actors in the pool at the same time.
    */
//  pool ! Broadcast(BroadcastMessage)

  Thread.sleep(5000)

  Await.result(system.terminate(), Duration.Inf)
}