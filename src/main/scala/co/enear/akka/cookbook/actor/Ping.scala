package co.enear.akka.cookbook.actor

import akka.actor.{Actor, ActorLogging, PoisonPill}

class Ping extends Actor with ActorLogging {
  var countdown = 10

  /**
    * Receive is the behavior of the actor.
    * Defines what messages the actor processes.
    */
  override def receive: Receive = {
    case PingMessage if countdown == 0 =>
      log.info("Stopping pings!")

      /**
        * Enqueues a message which kills the receiver actor when processed.
        * This means subsequent messages will be dropped.
        */
      sender ! PoisonPill

      // Stops this actor.
      context.stop(self)

    case PingMessage =>
      log.info("Ping!")
      countdown -= 1

      /**
        * sender() exists inside an Actor to respond to the
        * sender of the current message.
        */
      sender ! PongMessage
  }
}
