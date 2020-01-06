package actor

import actor.A02_Tell_Pattern.Protocol.Info
import akka.actor.{Actor, ActorLogging, ActorSystem, Props}

object A02_Tell_Pattern extends App {

  //This pattern is useful when you need to send a message to an actor, but do not expect to receive a response.
  //As a result, it is also commonly referred to as "fire and forget".

  println("Step 1: create an actor system")
  val system = ActorSystem("ActorSystem")

  println("\nStep 2: Define the message passing protocol for our DonutStoreActor")

  //Case classes are good for modeling immutable data
  object Protocol {
    case class Info(name: String)
  }

  println("\nStep 3: Define an actor")
  class executor extends Actor with ActorLogging {

    def receive = {
      case Info(name) =>
        log.info(s"Found $name")
    }
  }

  println("\nStep 4: Create InfoActor")
  val InfoActor = system.actorOf(Props[executor], name = "executor")

  println("\nStep 5: Akka tell Pattern")
  InfoActor ! Info("vanilla")

  println("\nStep 6: close the actor system")
  val isTerminated = system.terminate()
}
