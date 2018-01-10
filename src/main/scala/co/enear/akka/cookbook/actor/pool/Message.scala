package co.enear.akka.cookbook.actor.pool

sealed trait Message
case object SequenceMessage extends Message
case object BroadcastMessage extends Message