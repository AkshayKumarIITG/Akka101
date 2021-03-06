package routers

import akka.actor.SupervisorStrategy.{Escalate, Restart}
import akka.actor._
import akka.util.Timeout
import scala.concurrent.Future
import akka.pattern._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

import akka.routing.{DefaultResizer, ScatterGatherFirstCompletedPool}
import routers.R02_ScatterGatherFirstCompletedPool.Protocol.{CheckStock, WorkerFailedException}

object R02_ScatterGatherFirstCompletedPool extends App {

  println("Create an actor system")
  val system = ActorSystem("ActorSystem")

  println("\nDefine DonutStockActor")
  val donutStockActor = system.actorOf(Props[executor1], name = "DonutStockActor")

  /////////////////////////////////////////////////////////////////////////////////

  implicit val timeout = Timeout(5 seconds)

  val vanillaStockRequests = (1 to 2).map(i => (donutStockActor ? CheckStock("vanilla")).mapTo[Int])
  for {
    results <- Future.sequence(vanillaStockRequests)
  } yield println(s"vanilla stock results = $results")

  Thread.sleep(5000)

  ////////////////////////////////////////////////////////////////////////////////


  //Case classes are good for modeling immutable data
  object Protocol {
    case class Info(name: String)

    case class CheckStock(name: String)

    case class WorkerFailedException(error: String) extends Exception(error)
  }




  class executor1 extends Actor with ActorLogging {

    override def supervisorStrategy: SupervisorStrategy =
      OneForOneStrategy(maxNrOfRetries = 3, withinTimeRange = 5 seconds) {
        case _: WorkerFailedException =>
          log.error("Worker failed exception, will restart.")
          Restart

        case _: Exception =>
          log.error("Worker failed, will need to escalate up the hierarchy")
          Escalate
      }


    val workerName = "DonutStockWorkerActor"
    val resizer = DefaultResizer(lowerBound = 5, upperBound = 10)
    val props = ScatterGatherFirstCompletedPool(
      nrOfInstances = 5,
      resizer = Some(resizer),
      supervisorStrategy = supervisorStrategy,
      within = 5 seconds
    ).props(Props[executor2])

    val donutStockWorkerRouterPool: ActorRef = context.actorOf(props, "DonutStockWorkerRouter")

    def receive = {
      case checkStock @ CheckStock(name) =>
        log.info(s"Checking stock for $name donut")
        // We forward any work to be carried by worker actors within the pool
        donutStockWorkerRouterPool forward checkStock
    }

  }




  class executor2 extends Actor with ActorLogging {

    override def postRestart(reason: Throwable): Unit = {
      log.info(s"restarting ${self.path.name} because of $reason")
    }

    def receive = {
      case CheckStock(name) =>
        sender ! findStock(name)
    }

    def findStock(name: String): Int = {
      log.info(s"Finding stock for donut = $name, thread = ${Thread.currentThread().getId}")
      100
    }
  }


  println("\nStep 6: close the actor system")
  val isTerminated = system.terminate()
}
