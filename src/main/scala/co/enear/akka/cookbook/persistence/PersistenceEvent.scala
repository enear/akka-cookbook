package co.enear.akka.cookbook.persistence

sealed trait PersistenceEvent
case object IncrementEvent extends PersistenceEvent
case object DecrementEvent extends PersistenceEvent
