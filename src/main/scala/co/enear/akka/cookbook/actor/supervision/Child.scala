package co.enear.akka.cookbook.actor.supervision

import akka.actor.{Actor, ActorLogging}

class Child extends Actor with ActorLogging {
  override def receive: Receive = {
    case TeachMeHowToLive =>
      log.info("Movin' like Jagger!")
      throw new Overdose
  }

  override def preStart(): Unit = { // there are other flavors of methods for managing actor lifecycle
    log.info("Born in the USA!")
  }

  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    log.info("From the ashes!")
  }
}
