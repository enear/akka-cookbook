package co.enear.akka.cookbook.stream

import akka.actor.ActorSystem
import akka.stream.Supervision.{Decider, Resume}
import akka.stream._
import akka.stream.scaladsl.{Balance, Flow, GraphDSL, Merge, Sink, Source}

/**
  * Akka streams is built using actors.
  *
  * They have back-pressure mechanisms enabled by default.
  * This means downstream elements signal upstream elements
  * that they are ready to receive messages.
  *
  * A stream can be seen as a graph. The basic building block
  * of a stream is the GraphStage.
  * A graph stage can be one of Source (one output), Sink (one input)
  * and Flow (n inputs n outputs) and they can be connected in any ways.
  */
object StreamApp extends App {
  implicit val system = ActorSystem("stream")

  /**
    * A decider can be defined on a stream or stage to handle failures.
    * The default is to stop the stream on failure.
    */
  val decider: Decider = {
    case e: Exception =>
      println(s"Caught exception: ${e.getMessage}. Resuming the stream.")
      Resume
  }

  /**
    * Specify a decider for the whole stream.
    */
  val settings = ActorMaterializerSettings(system)
    .withSupervisionStrategy(decider)

  /**
    * An ActorMaterializer creates the actors necessary to
    * run a stream.
    */
  implicit val materializer = ActorMaterializer(settings)

  /**
    * Create a Source, which is the input to the stream.
    * This one is created from the basic building block, the Graph.
    * There are many pre-made graphs.
    */
  val source = Source.fromGraph(new HelloSource)

  /**
    * Create a Sink, which is the output of the stream.
    *
    */
  val sink = Sink.fromGraph(new HelloSink)

  /**
    * A Flow, which has an input and output.
    * This Flow has a supervision strategy to handle failure.
    */
  val flow = Flow[String].mapConcat(_.split(" ").toList)
    .withAttributes(ActorAttributes.supervisionStrategy(decider))

  /**
    * A parallel flow which runs in three actors.
    * It has only an input and output, but internally it splits
    * the stream into three flows which run in three actors.
    *
    * Balance splits upstream into n stages evenly.
    * Merge joins upstream into one stage.
    * Broadcast sends all messages downstream.
    *
    * .async runs the stage in a new actor.
    */
  val parallelFlow = Flow.fromGraph(
    GraphDSL.create() { implicit builder =>
      import GraphDSL.Implicits._

      val balance = builder.add(Balance[String](3))
      val merge = builder.add(Merge[String](3))
//      val broadcast = builder.add(Broadcast[String](3))

      balance.out(0) ~> flow.async ~> merge.in(0)
      balance.out(1) ~> flow.async ~> merge.in(1)
      balance.out(2) ~> flow.async ~> merge.in(2)

      FlowShape(balance.in, merge.out)
  })

  /**
    * Create a blueprint of the stream.
    */
  val stream = source
    /**
      * The whole stream runs in a single actor.
      */
    //    .via(flow)
    /**
      * The flow runs on a different actor.
      */
    //    .via(flow.async)
    /**
      * The flow runs on many different actors, as defined in the parallelFlow.
      */
    .via(parallelFlow) // parallel puts more async boundaries
    .to(sink)

  stream.run()
}



