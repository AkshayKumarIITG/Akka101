import akka.actor.ActorSystem

import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global


object A01_Introduction extends App {

  //An ActorSystem is a 'black' box that will eventually hold the actors, and allow to interact with them.
  println("Step 1: create an actor system")
  val system = ActorSystem("ActorSystem")



  println("\nStep 2: close the actor system")
  val isTerminated = system.terminate()

  //onComplete -> Scala Future

  println("\nStep 3: Check the status of the actor system")
  isTerminated.onComplete {
    case Success(result) => println("Successfully terminated actor system")
    case Failure(e)     => println("Failed to terminate actor system")
  }
  Thread.sleep(5000)
}
