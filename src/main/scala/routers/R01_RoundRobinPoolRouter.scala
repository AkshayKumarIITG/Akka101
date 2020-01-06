package routers

import akka.actor.SupervisorStrategy.{Escalate, Restart}
import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, OneForOneStrategy, Props, SupervisorStrategy}
import akka.routing.{DefaultResizer, RoundRobinPool}
import akka.util.Timeout
import routers.R01_RoundRobinPoolRouter.Protocol.{CheckStock, WorkerFailedException}

import scala.concurrent.duration._
import akka.pattern._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object R01_RoundRobinPoolRouter extends App{

  println("Create an actor system")
  val system = ActorSystem("ActorSystem")

  val donutStockActor = system.actorOf(Props[executor1], name = "DonutStockActor")




  implicit val timeout = Timeout(5 seconds)
  val vanillaStockRequests = (1 to 10).map(i => (donutStockActor ? CheckStock("vanilla")).mapTo[Int])

  for {
    results <- Future.sequence(vanillaStockRequests)
  } yield println(s"vanilla stock results = $results")

  Thread.sleep(5000)



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


    val resizer = DefaultResizer(lowerBound = 5, upperBound = 10)
    val props = RoundRobinPool(5, Some(resizer), supervisorStrategy = supervisorStrategy).props(Props[executor2])
    val donutStockWorkerRouterPool: ActorRef = context.actorOf(props, "DonutStockWorkerRouter")

    def receive = {
      case checkStock @ CheckStock(name) =>
        log.info(s"Checking stock for $name donut")
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
