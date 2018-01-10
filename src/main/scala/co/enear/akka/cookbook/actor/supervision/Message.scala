package co.enear.akka.cookbook.actor.supervision

sealed trait Message
case object MakeChild extends Message
case object SweetChildOfMine extends Message
case object TeachMeHowToLive extends Message
case object PayForTheirStudies extends Message