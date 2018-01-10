package co.enear.akka.cookbook.actor.supervision

import akka.actor.{Actor, ActorLogging}

class Child extends Actor with ActorLogging {
  override def receive: Receive = {
    case TeachMeHowToLive =>
      /**
        * Failure will be propagated to the supervisor.
        */
      throw new Overdose
  }

  /**
    * There are many pre and post methods
    * for managing actor lifecycle.
    */
  override def preStart(): Unit = {
    log.info("Starting child.")
  }

  override def preRestart(reason: Throwable,
                          message: Option[Any]): Unit = {
    log.info("Restarting child.")
  }
}
