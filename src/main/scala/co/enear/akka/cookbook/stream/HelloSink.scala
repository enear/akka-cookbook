package co.enear.akka.cookbook.stream

import akka.stream.{Attributes, Inlet, SinkShape}
import akka.stream.stage.{GraphStage, GraphStageLogic, InHandler, TimerGraphStageLogic}
import scala.concurrent.duration._

class HelloSink extends GraphStage[SinkShape[String]] {

  val in: Inlet[String] = Inlet("HelloSink")

  /**
    * A SinkShape has just one input. The only input
    * we pass to it is the one Inlet.
    */
  override def shape: SinkShape[String] = SinkShape(in)

  /**
    * A TimerGraph allows doing scheduling.
    */
  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic = new TimerGraphStageLogic(shape) {
    var counts: Map[String, Int] =
      Map.empty[String, Int].withDefaultValue(0)

    /**
      * Set the handler for the one Inlet we defined for the sink.
      */
    setHandler(in, new InHandler {
      override def onPush(): Unit = {
        /**
          * Returns the element which comes from the stream.
          */
        val word = grab(in)
        counts += word -> (counts(word) + 1)

        /**
          * Ask for the next element from upstream.
          * This calls onPull() from the upstream stage.
          */
        pull(in)
      }
    })

    override def preStart(): Unit = {
      /**
        * Set a timer with a non existing key.
        */
      schedulePeriodically(None, 1.seconds)

      /**
        * Start by pulling the upstream element,
        * so that when we grab, it was asked for already.
        */
      pull(in)
    }

    override protected def onTimer(timerKey: Any): Unit =
      println(s"Calling timer with counts: $counts.")
  }
}