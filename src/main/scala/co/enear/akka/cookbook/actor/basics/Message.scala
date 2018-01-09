package co.enear.akka.cookbook.actor.basics

sealed trait Message
case object PingMessage extends Message
case object PongMessage extends Message