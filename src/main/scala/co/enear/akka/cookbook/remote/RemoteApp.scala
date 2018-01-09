package co.enear.akka.cookbook.remote

import java.util.concurrent.TimeUnit

import akka.actor.{ActorRef, ActorSystem, Address, Deploy, Props}
import akka.remote.RemoteScope
import akka.pattern.ask
import akka.util.Timeout
import co.enear.akka.cookbook.actor.basics.{Pong, PongMessage}

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global

object RemoteApp extends App {
  val system = ActorSystem("remote")

  /**
    * Create an actor in some arbitrary JVM.
    * Note: we are creating it on this one. See application.conf.
    */
  val address = Address("akka.tcp", "remote", "localhost", 2552)
  val actorName = "remoteActor"
  val remote = system.actorOf(
    Props[Pong].withDeploy(
      Deploy(scope = RemoteScope(address))
    ),
    actorName
  )

  implicit val timeout = Timeout(30, TimeUnit.SECONDS)
  remote ? PongMessage

  /**
    * Get an actor running in some arbitrary JVM.
    * All our actors live under the /user guardian.
    * Note: We obtain it from this one.
    */
  val remoteActor: Future[ActorRef] = system
    .actorSelection(s"akka.tcp://remote@localhost:2552/user/$actorName")
    .resolveOne(10.seconds)

  remoteActor.map(actor => actor ? PongMessage)

  Thread.sleep(5000)

  Await.result(system.terminate(), Duration.Inf)
}
