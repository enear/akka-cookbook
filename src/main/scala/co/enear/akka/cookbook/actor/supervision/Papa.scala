package co.enear.akka.cookbook.actor.supervision

import akka.actor.SupervisorStrategy.{Escalate, Restart}
import akka.actor.{Actor, ActorLogging, ActorRef, OneForOneStrategy, Props, SupervisorStrategy, Terminated}

import scala.collection.mutable.ListBuffer
import scala.concurrent.duration._

class Papa extends Actor with ActorLogging {
  var children: ListBuffer[ActorRef] = ListBuffer.empty

  override def receive: Receive = {
    case MakeChild =>

      /**
        * This creates a child actor supervised by this.
        * When this actor is stopped, all children are stopped.
        */
      val child = context.actorOf(Props[Child])
      children += child

    case SweetChildOfMine =>
      val favoriteChild = context.actorOf(Props[FavoriteChild])
      children += favoriteChild

      /**
        * Watching a child allows matching on a Terminated message.
        */
      context.watch(favoriteChild)

    case PayForTheirStudies =>
      children.foreach(_ ! TeachMeHowToLive)

    /**
      * Requires child to be in context.watch()
      */
    case Terminated(actor) =>
      log.info(s"I cared so much for you $actor...")
  }

  override def supervisorStrategy: SupervisorStrategy = {
    /**
      * OneForOneStrategy is a failure strategy for single isolated actors.
      * It allows matching on exceptions and handling them.
      * Alternatively AllForOneStrategy is a failure strategy
      * for all children recursively.
      */
    OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1.minute) {
      /**
        * Simply restart the child immediately.
        */
      case _: Overdose => Restart

      /**
        * Propagate failure to this supervisors supervisor.
        */
      case _ => Escalate
    }
  }
}