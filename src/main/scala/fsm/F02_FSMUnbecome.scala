package fsm

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}

object F02_FSMUnbecome extends App {

  println("Step 1. Create ActorSystem")
  val system = ActorSystem("ActorState")



  println("\nStep 3: Create DonutBakingActor")
  val bakingActor = system.actorOf(Props[DonutBakingActor], "donut-baking-actor")
  bakingActor ! "boom" // not valid
  Thread.sleep(2000)



  println("\nStep 4: Send events to actor to switch states and process events")
  bakingActor ! "BakeDonut"
  Thread.sleep(2000)


  bakingActor ! "BakePlain"
  Thread.sleep(2000)



  bakingActor ! "BakeVanilla"
  Thread.sleep(2000)



  bakingActor ! "Bake Chocolate"
  Thread.sleep(2000)



  bakingActor ! "StopBaking"
  Thread.sleep(2000)



  bakingActor ! "BakeVanilla"
  Thread.sleep(2000)



  println("\nStep 5: Shutdown actor system")
  system.terminate()



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
