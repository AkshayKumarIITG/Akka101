package fsm

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}

object F01_FSMBecome extends App {

  println("Step 1: Create an actor system")
  val system = ActorSystem("ActorStateBecome")

  print("\nStep 3: Create DonutBakingActor")
  val bakingActor = system.actorOf(Props[DonutBakingActor], "donut-baking-actor")




  println("\nStep 4: Send events to actor to switch states and process events")
  bakingActor ! "boom" // not valid
  Thread.sleep(2000)


  bakingActor ! "BakeDonut"
  Thread.sleep(2000)


  bakingActor ! "BakePlain"
  Thread.sleep(2000)


  bakingActor ! "BakeVanilla"
  Thread.sleep(2000)


  bakingActor ! "Bake Chocolate"
  Thread.sleep(2000)




  println("\nStep 2: Define DonutBakingActor with become() states")
  class DonutBakingActor extends Actor with ActorLogging {
    import context._

    def receive = {
      case "BakeDonut" =>
        log.info("Becoming BakeDonut state ")
        become {
          case "BakeVanilla" =>
            log.info("baking vanilla")

          case "BakePlain" =>
            log.info("Baking Plain")

          case event @ _ =>
            log.info(s"Allowed events [BakeVanilla, BakePlain], event = $event")
        }

      case event @ _ =>
        log.info(s"Allowed events [BakeDonut], events = $event")

    }
  }


  system.terminate()
}
