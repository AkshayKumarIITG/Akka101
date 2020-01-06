package actor

import actor.A03_Ask_Pattern.Protocol.Info
import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.util.Timeout
import akka.pattern._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object A03_Ask_Pattern extends App{

  println("Step 1: create an actor system")
  val system = ActorSystem("ActorSystem")

  println("\nStep 4: Create an InfoActor")
  val InfoActor = system.actorOf(Props[executor], name = "executor")


  println("\nStep 5: Akka tell Pattern")
  implicit val timeout: Timeout = Timeout(5 seconds)

  val vanillaFound = InfoActor ? Info("vanilla")
  for {
    found <- vanillaFound
  } yield println(s"Dount found = $found")

  val glazedFound = InfoActor ? Info("glazed")
  for {
    found <- glazedFound
  } yield println(println(s"Glazed donut found = $found"))

  println("\nStep 2: Define the message passing protocol for our DonutStoreActor")

  //Case classes are good for modeling immutable data
  object Protocol {
    case class Info(name: String)
  }

  println("\nStep 3: Define an actor")
  class executor extends Actor with ActorLogging {

    def receive = {
      case Info(name) if name == "vanilla" =>
        log.info(s"Found valid item $name")
        sender ! true

      case Info(name) =>
      log.info(s"$name is not supported")
      sender ! false
    }
  }

  println("\nStep 6: close the actor system")
  val isTerminated = system.terminate()

}
