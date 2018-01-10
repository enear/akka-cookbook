package co.enear.akka.cookbook.actor.supervision

import akka.actor.{Actor, ActorLogging}

class FavoriteChild extends Actor with ActorLogging {
  override def receive: Receive = {
    case TeachMeHowToLive =>

      /**
        * This actor will stop itself by sending a stop message to itself.
        * This guarantees that after the current message is done,
        * we receive the stop message, meaning other queued messages will
        * get dropped.
        */
      context.stop(self)
  }
}