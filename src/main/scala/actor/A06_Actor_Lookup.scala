package actor

import actor.A06_Actor_Lookup.Protocol.Info
import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.util.Timeout
import akka.pattern._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

object A06_Actor_Lookup extends App {

  println("Step 1: create an actor system")
  val system = ActorSystem("ActorSystem")

  println("\nStep 4: Create an InfoActor")
  val InfoActor = system.actorOf(Props[executor], name = "executor")



  system.actorSelection("/user/InfoActor") ! Info("chocolate")

  //Case classes are good for modeling immutable data
  object Protocol {
    case class Info(name: String)
  }

  class executor extends Actor with ActorLogging {

    def receive = {
      case Info(name) =>
        log.info(s"Found $name donut")
    }
  }

  println("\nStep 6: close the actor system")
  val isTerminated = system.terminate()
}
