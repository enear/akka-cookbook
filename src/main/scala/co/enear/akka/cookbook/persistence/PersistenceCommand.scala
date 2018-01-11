package co.enear.akka.cookbook.persistence

sealed trait PersistenceCommand
case object Increment extends PersistenceCommand
case object Decrement extends PersistenceCommand
case object Snapshot extends PersistenceCommand
case object Log extends PersistenceCommand
case object Shutdown extends PersistenceCommand
