package co.enear.akka.cookbook.stream

import akka.actor.SupervisorStrategy.{Restart, Resume, Stop}
import akka.stream.ActorAttributes.SupervisionStrategy
import akka.stream.stage.{GraphStage, GraphStageLogic, OutHandler}
import akka.stream.{Attributes, Outlet, SourceShape}

import scala.util.Random

class HelloSource extends GraphStage[SourceShape[String]] {

  val out: Outlet[String] = Outlet("HelloSource")

  /**
    * SourceShape defines a stage with a single output.
    */
  override def shape: SourceShape[String] = SourceShape(out)

  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic = new GraphStageLogic(shape) {

    /**
      * By implementing the decider logic, we enable Supervision
      * for the users of our stage.
      */
    def decider = inheritedAttributes
      .get[SupervisionStrategy]
      .map(_.decider)
      .getOrElse { _: Throwable => Stop }

    /**
      * Set the handler for our only output.
      */
    setHandler(out, new OutHandler {

      /**
        * This function will be called when the downstream
        * stage calls its pull().
        */
      override def onPull(): Unit = {
        val random = Random.nextInt() % 1000000 > 999998

        /**
          * Push the value to downstream.
          */
        def p(): Unit = push(out, "Hello, is it me you're looking for?")

        /**
          * Decider logic for supervision of failures goes here.
          */
        val ex = new Exception("Failed pulling element from the stream.")
        if (random) decider(ex) match {
          case Stop => failStage(ex)
          case Resume => p()
          case Restart => p()
        }
      }
    })
  }
}