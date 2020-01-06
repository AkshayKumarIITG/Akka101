package fsm

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}

import scala.io.StdIn

object F10_Scheduler extends App {

  println("Step 1. Create ActorSystem")
  val system = ActorSystem("ActorState")




  println("\nStep 3: Create DonutBakingActor")
  val bakingActor = system.actorOf(Props[DonutBakingActor], "donut-baking-actor")



  println("\nStep 4: Send events to actor to switch states and process events")
  bakingActor ! "BakeDonut"
  Thread.sleep(2000)



  println("\nStep 5: Using system.scheduler to send periodic events to actor")
  import system.dispatcher
  import scala.concurrent.duration._

  system.scheduler.schedule(1.seconds, 2.seconds) {
    bakingActor ! "BakeVanilla"
    Thread.sleep(1000)
  }

  StdIn.readLine()




  println("\nStep 2: Define DonutBakingActor with become() and unbecome() states")
  class DonutBakingActor extends Actor with ActorLogging {
    import context._

    def receive = {
      case "BakeDonut" =>
        log.info("becoming bake state")
        become {
          case "BakeVanilla" =>
            log.info("baking vanilla")

          case "BakePlain" =>
            log.info("baking plain")

          case "StopBaking" =>
            log.info("stopping to bake")
            unbecome()

          case event @ _ =>
            log.info(s"Allowed events [BakeVanilla, BakePlain, StopBaking], event = $event")
        }

      case event @ _ =>
        log.info(s"Allowed events [BakeDonut], event = $event")
    }
  }
}
