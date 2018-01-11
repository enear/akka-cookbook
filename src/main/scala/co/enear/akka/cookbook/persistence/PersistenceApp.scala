package co.enear.akka.cookbook.persistence

import akka.actor.{ActorSystem, Props}
import akka.persistence.query.PersistenceQuery
import akka.persistence.query.journal.leveldb.scaladsl.LeveldbReadJournal
import akka.stream.ActorMaterializer

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * Akka persistence allows storing and recovering actor state.
  * Note: Running this app multiple times will yield different results.
  */
object PersistenceApp extends App {
  implicit val system = ActorSystem("persistence")

  /**
    * The required unique persistence id for a given actor.
    */
  val id = "so-unique"
  val persistent = system.actorOf(Props(classOf[ImPersistent], id))

  persistent ! Log
  persistent ! Increment
  persistent ! Log
  persistent ! Decrement
  persistent ! Log
  persistent ! Increment
  persistent ! Snapshot
  persistent ! Increment
  persistent ! Increment
  persistent ! Log

  Thread.sleep(5000) // Wait for all messages to be processed

  persistent ! Shutdown

  /**
    * Restarting the persistent actor with the same id.
    */
  system.log.info("Reviving the actor.")
  val persistentReborn = system.actorOf(Props(classOf[ImPersistent], id))

  /**
    * The following is part of akka persistence query.
    * We can query the persistent actors for a given actor system.
    */
  implicit val materializer = ActorMaterializer()
  val queries = PersistenceQuery(system)
    .readJournalFor[LeveldbReadJournal](LeveldbReadJournal.Identifier)
  queries.persistenceIds()
    .runForeach(s => system.log.info(s"Showing the id of stored events: $s"))

  Thread.sleep(5000)

  Await.result(system.terminate(), Duration.Inf)
}