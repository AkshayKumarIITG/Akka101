package actor

import actor.A07_Child_Actors.Protocol.Info
import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.util.Timeout
import akka.pattern._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

object A07_Child_Actors extends App {

  println("Step 1: create an actor system")
  val system = ActorSystem("ActorSystem")

  println("\nStep 4: Create an InfoActor")
  val InfoActor = system.actorOf(Props[executor1], name = "executor")


  InfoActor ! Info("vanilla")

  //Case classes are good for modeling immutable data
  object Protocol {
    case class Info(name: String)
  }


  class executor1 extends Actor with ActorLogging {

    val bakingActor = context.actorOf(Props[executor2], name="BakingActor")

    def receive = {
      case msg @ Info(name) =>
        log.info(s"Found $name donut")
        bakingActor forward msg
    }
  }

  class executor2 extends Actor with ActorLogging {

    def receive = {
      case Info(name) =>
        log.info(s"BakingActor baking $name donut")
    }
  }


  println("\nStep 6: close the actor system")
  val isTerminated = system.terminate()

}
