package actor

import actor.A05_Ask_Pattern_pipeTo.Protocol.{CheckStock, Info}
import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.util.Timeout
import akka.pattern._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

object A05_Ask_Pattern_pipeTo extends App {

  println("Step 1: create an actor system")
  val system = ActorSystem("ActorSystem")

  println("\nStep 4: Create an InfoActor")
  val InfoActor = system.actorOf(Props[executor], name = "executor")



  implicit val timeout = Timeout(5 second)

  val vanillaDonutStock: Future[Int] = (InfoActor ? CheckStock("vanilla")).mapTo[Int]
  for {
    found <- vanillaDonutStock
  } yield println(s"Vanilla donut stock = $found")


  //Case classes are good for modeling immutable data
  object Protocol {
    case class Info(name: String)

    case class CheckStock(name: String)
  }

  class executor extends Actor with ActorLogging {

    def receive = {
      case CheckStock(name) =>
        log.info(s"Checking stock for $name donut")
        findStock(name).pipeTo(sender)
    }

    def findStock(name: String): Future[Int] = Future {
      100
    }
  }

  println("\nStep 6: close the actor system")
  val isTerminated = system.terminate()

}
