package co.enear.akka.cookbook.actor.supervision

import akka.actor.{Actor, ActorLogging}

class FavoriteChild extends Actor with ActorLogging {
  override def receive: Receive = {
    case TeachMeHowToLive =>
      log.info("Yes, sir!")
      context.stop(self)
  }
}