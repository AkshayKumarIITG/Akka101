package Dispatcher

import java.util.concurrent.TimeUnit

import Dispatcher.D03_FixedThreadPoolDispatcher.Protocol.{DonutStockRequest, StockRequest}
import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.util.Timeout

import scala.concurrent.duration._
import akka.pattern._

import scala.concurrent.{Await, Future}

object D03_FixedThreadPoolDispatcher extends App {

  println("Step 1: Create actor system")
  val system = ActorSystem("DonutStoreActorSystem")



  println("\nStep 2: Create fixed thread pool configuration in application.conf")


  println("Step 3: Lookup our fixed-thread-pool dispatcher from application.conf")
  implicit val timeout = Timeout(1, TimeUnit.MINUTES)
  implicit val executionContext = system.dispatchers.lookup("fixed-thread-pool")

  val clientRequests = (1 to 10).map(i => StockRequest(s"vanilla donut", i))
  val futures = clientRequests.map{ stock =>
    val actorRef = system.actorOf(Props[DonutStockRequestActor].withDispatcher("fixed-thread-pool"))
    (actorRef ? stock).mapTo[DonutStockRequest]
  }

  val results = Await.result(Future.sequence(futures), 1 minute)
  results.foreach(println(_))

  object Protocol {
    case class StockRequest(name: String, clientId: Int)

    trait Result
    case class DonutStockRequest(quantity: Int) extends Result
    case class DonutFailure(msg: String) extends Result
  }




  println("\nStep 5: Create DonutStockRequestActor")
  class DonutStockRequestActor extends Actor with ActorLogging {
    val randomStock = scala.util.Random
    def receive = {
      case StockRequest(name, clientId) =>
        log.info(s"CHECKING: donut stock for name = $name, clientId = $clientId")
        Thread.sleep(5000)
        log.info(s"FINISHED: donut stock for name = $name, clientId = $clientId")
        sender() ! DonutStockRequest(randomStock.nextInt(100))
    }
  }

  println("\nStep 8: Close actor system")
  system.terminate()
}
