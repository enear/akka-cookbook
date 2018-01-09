package co.enear.akka.cookbook.actor.supervision

sealed trait Message
case object SweetChildOfMine extends Message
case object TeachMeHowToLive extends Message