package co.enear.akka.cookbook.actor

sealed trait Message
case object PingMessage extends Message
case object PongMessage extends Message

