package co.enear.akka.cookbook.actor.supervision

import akka.actor.SupervisorStrategy.{Escalate, Restart}
import akka.actor.{Actor, ActorLogging, ActorRef, OneForOneStrategy, Props, SupervisorStrategy, Terminated}
import scala.concurrent.duration._
import scala.collection.mutable.ListBuffer

class Papa extends Actor with ActorLogging {
  var children: ListBuffer[ActorRef] =
    ListBuffer[ActorRef]() // maintain a list of child actors

  override def receive: Receive = {
    case SweetChildOfMine =>
      log.info("Whole lotta love!")
      def withMama = context.actorOf(Props[Child]) // create a child actor supervised by this
      children ++= List(withMama, withMama)
      children.foreach(r => r ! TeachMeHowToLive)

      val favoriteChild = context.actorOf(Props[FavoriteChild])
      context.watch(favoriteChild) // watching an actor allows matching on Terminated
      favoriteChild ! TeachMeHowToLive

    case Terminated(actor) => log.info(s"I cared so much for you $actor...") // watch an actor to use this
  }

  override def supervisorStrategy: SupervisorStrategy = {
    OneForOneStrategy(10, 1.minute) { // a failure strategy for single isolated actors
      //      AllForOneStrategy(10, 1.minute) { // a failure strategy for all children recursively
      case _: Overdose => Restart // restart the child
      case _ => Escalate // send failure to the supervisors supervisor
    }
  }
}