package co.enear.akka.cookbook.actor.pool

import akka.actor.{Actor, ActorLogging}

class PoolBoy extends Actor with ActorLogging {
  override def receive: Receive = {
    case SequenceMessage =>
      Thread.sleep(200)
      log.info(s"I'm in a pool ${self.path.name}.")
    case BroadcastMessage =>
      log.info(s"This was a broadcast to ${self.path.name}.")
  }
}