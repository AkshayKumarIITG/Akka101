package actor

import actor.A10_Error_Kernel_Supervision.Protocol.{CheckStock, Info, WorkerFailedException}
import akka.actor.SupervisorStrategy.{Escalate, Restart}
import akka.actor.{Actor, ActorLogging, ActorSystem, OneForOneStrategy, PoisonPill, Props, SupervisorStrategy}
import akka.util.Timeout
import akka.pattern._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

object A10_Error_Kernel_Supervision extends App {

  println("Step 1: create an actor system")
  val system = ActorSystem("ActorSystem")

  println("\nStep 4: Create an InfoActor")
  val InfoActor = system.actorOf(Props[executor1], name = "executor")


  implicit val timeout = Timeout(5 second)

  val vanillaDonutStock: Future[Int] = (InfoActor ? CheckStock("vanilla")).mapTo[Int]
  for {
    found <- vanillaDonutStock
  } yield (println(s"Vanilla donut stock = $found"))


  //Case classes are good for modeling immutable data
  object Protocol {
    case class Info(name: String)

    case class CheckStock(name: String)

    case class WorkerFailedException(error: String) extends Exception(error)
  }


  class executor1 extends Actor with ActorLogging {

    override def supervisorStrategy: SupervisorStrategy = OneForOneStrategy(maxNrOfRetries = 3, withinTimeRange = 1 seconds) {
      case _: WorkerFailedException =>
        log.error("Worker failed exception, will restart.")
        Restart

      case _: Exception =>
        log.error("Worker failed, will need to escalate up the hierarchy")
        Escalate
    }

    val bakingActor = context.actorOf(Props[executor2], name="BakingActor")

    def receive = {
      case msg @ CheckStock(name) =>
        log.info(s"Found $name donut")
        bakingActor forward msg
    }
  }

  class executor2 extends Actor with ActorLogging {

    @throws[Exception](classOf[Exception])
    override def postRestart(reason: Throwable): Unit = {
      log.info(s"restarting ${self.path.name} because of $reason")
    }

    def receive = {
      case CheckStock(name) =>
        findStock(name)
        context.stop(self)
    }

    def findStock(name: String): Int = {
      100

      throw new WorkerFailedException("boom")
    }
  }


  println("\nStep 6: close the actor system")
  val isTerminated = system.terminate()

}
