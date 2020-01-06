package actor
import actor.A04_Ask_Pattern_mapTo.Protocol.Info
import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.util.Timeout
import akka.pattern._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
object A04_Ask_Pattern_mapTo extends App {

  println("Step 1: create an actor system")
  val system = ActorSystem("ActorSystem")

  println("\nStep 4: Create an InfoActor")
  val InfoActor = system.actorOf(Props[executor], name = "executor")


  implicit val timeout = Timeout(5 second)
  val vanillaDonutFound: Future[Boolean] = (InfoActor ? Info("error")).mapTo[Boolean]

  for {
    found <- vanillaDonutFound
  } yield println(s"Vanilla donut found = $found")


  //Case classes are good for modeling immutable data
  object Protocol {
    case class Info(name: String)
  }

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
