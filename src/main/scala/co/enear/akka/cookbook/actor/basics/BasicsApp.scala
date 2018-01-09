package co.enear.akka.cookbook.actor.basics

import java.util.concurrent.TimeUnit

import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.Await
import scala.concurrent.duration._

/**
  * Actors have state, behavior, a mailbox and send and receive messages.
  * They do not occupy a thread until they receive a message.
  * They do not occupy the main thread.
  */
object BasicsApp extends App {
  /**
    * ActorSystem creates a heavyweight structure for a
    * given application, which handles actors living in n threads.
    */
  val system = ActorSystem("pingpong")

  /**
    * Creates an actor.
    */
  val ping = system.actorOf(Props[Ping])
  val pong = system.actorOf(Props[Pong])

  /**
    * Send a message to an actor.
    */
  implicit val timeout = Timeout(30, TimeUnit.SECONDS)
  val message = Await.result(ping ? PingMessage, Duration.Inf)

  /**
    * Make an actor send a message to another.
    */
  pong.tell(PongMessage, ping)

  Thread.sleep(5000)

  Await.result(system.terminate(), Duration.Inf)
}
