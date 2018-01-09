package co.enear.akka.cookbook.actor

import akka.actor.{Actor, ActorLogging}

class Pong extends Actor with ActorLogging {

  override def receive: Receive = {
    case PongMessage =>
      log.info("Pong!")
      sender ! PingMessage

      /**
        * Specify a new behavior for the actor.
        * Essentially, make it use a different function to process messages.
        */
      context.become(pong)
  }

  def pong: Receive = {
    case PongMessage =>
      log.info("PONG!")
      sender ! PingMessage
      context.become(receive)
  }
}