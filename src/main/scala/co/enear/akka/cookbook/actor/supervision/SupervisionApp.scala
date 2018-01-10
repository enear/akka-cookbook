package co.enear.akka.cookbook.actor.supervision

import akka.actor.{ActorSystem, Props}
import akka.pattern.BackoffSupervisor

import scala.concurrent.Await
import scala.concurrent.duration._

/**
  * An actor can be killed with stop(), PoisonPill and Kill.
  * stop() and PoisonPill stop the actor and children when received.
  * stop() is guaranteed to be put in front of the mailbox.
  * PoisonPill will be the last message on the mailbox.
  * Kill makes the actor throw an ActorKilledException to be handled by supervision.
  */
object SupervisionApp extends App {
  val system = ActorSystem("supervision")

  val parent = system.actorOf(Props[Papa])

  /**
    * Starts two Child and one FavoriteChild.
    * When PayForTheirStudies is sent, Child will get restarted
    * through SupervisionStrategy and FavoriteChild termination
    * will be observed through Terminated.
    */
  parent ! MakeChild
  parent ! MakeChild
  parent ! SweetChildOfMine // clearly a favorite
  parent ! PayForTheirStudies

  /**
    * A BackoffSupervisor spawns two actors, a supervisor and a child.
    * When the child fails, the supervisor attempts to restart it
    * after some time as specified below.
    * Note: nothing is done here with this actor.
    */
  val backoffSupervisor = BackoffSupervisor.props(
    Props[Papa],
    childName = "papa",
    minBackoff = 5.seconds,
    maxBackoff = 30.seconds,
    randomFactor = 0.5
  )

  Thread.sleep(5000)

  Await.result(system.terminate(), Duration.Inf)
}
