package co.enear.akka.cookbook.persistence

import akka.actor.ActorLogging
import akka.persistence._

/**
  * Akka persistence uses event sourcing, shown here, in order to
  * restore state. Also makes use of snapshots to improve performance.
  *
  * PersistentActor is an actor that persists events to the journal and
  * when restarted recovers the state from the journal.
  */
class ImPersistent(id: String)
//                   recovery: Recovery) // this allows for custom recovery
  extends PersistentActor
    with ActorLogging {
  var state = 0

  /**
    * This is a function from events to new state.
    */
  def receiveEvent(persistenceEvent: PersistenceEvent): Any =
    persistenceEvent match {
      case DecrementEvent => state -= 1
      case IncrementEvent => state += 1
    }

  /**
    * When recovered from persistence, this is the
    * function that is called. It specifies the function used
    * to recover from a stream of events or from snapshots.
    */
  override def receiveRecover: Receive = {
    case x: PersistenceEvent => receiveEvent(x)
    case SnapshotOffer(metadata, snapshot: PersistenceSnapshot) =>
      log.info(metadata.toString)
      state = snapshot.state
    case RecoveryCompleted => log.info(s"My state is recovered: $state")
    case x => log.warning(x.toString)
  }

  /**
    * This is receive() for PersistentActor.
    * These are the messages the actor receives.
    * We can choose to reject them.
    * Or we can emit an Event and persist it or persist a snapshot of this actors state.
    */
  override def receiveCommand: Receive = {
    case Increment => persist(IncrementEvent)(receiveEvent)
    case Decrement => persist(DecrementEvent)(receiveEvent)
    case Snapshot => saveSnapshot(PersistenceSnapshot(state))
    case Log => log.info(s"My state is $state")
    case Shutdown => context.stop(self) // dont shutdown a persistent actor with PoisonPill
    case x: SaveSnapshotSuccess =>
      log.info(x.toString)
      deleteSnapshots(SnapshotSelectionCriteria(x.metadata.sequenceNr)) // delete old snapshots
    case x: SaveSnapshotFailure => log.error(x.toString)
    case x: DeleteSnapshotsSuccess => log.info(x.toString)
    case x: DeleteSnapshotsFailure => log.error(x.toString)
    case x => log.warning(x.toString)
  }

  /**
    * A persistent actor always has to be stored with an id.
    */
  override def persistenceId: String = id

  /**
    * Handling persistence failure.
    * These always stop the actor afterwards.
    */
//  onPersistFailure(???, ???, ???)
//  onRecoveryFailure(???, ???)
}