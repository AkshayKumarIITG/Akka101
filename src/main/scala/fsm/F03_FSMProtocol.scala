package fsm

import akka.actor.{Actor, ActorSystem, Props}

object F03_FSMProtocol extends App {

  println("Step 1. Create ActorSystem")
  val system = ActorSystem("ActorState")

  println("\nStep 4: Create DonutBakingActor")
  val bakingActor = system.actorOf(Props[DonutBakingActor], "donut-baking-actor")



  bakingActor ! "boom" // not valid
  Thread.sleep(2000)



  println("\nStep 5: Send events to actor to switch states and process events")
  bakingActor ! BakeDonut
  Thread.sleep(2000)



  bakingActor ! BakePlain
  Thread.sleep(2000)



  bakingActor ! BakeVanilla
  Thread.sleep(2000)



  bakingActor ! "Bake Chocolate"
  Thread.sleep(2000)



  bakingActor ! StopBaking
  Thread.sleep(2000)



  bakingActor ! BakeVanilla
  Thread.sleep(2000)



  println("\nStep 2: Define message passing protocol using sealed trait")
  sealed trait DonutProtocol
  case object BakeDonut extends DonutProtocol
  case object BakeVanilla extends DonutProtocol
  case object BakePlain extends DonutProtocol
  case object StopBaking extends DonutProtocol


  class DonutBakingActor extends Actor {

    import context._

    def startBaking: Receive = {
      case BakeDonut =>
        println("Becoming bake state")
        become(bake)

      case event @ _ =>
        println(s"Allowed events [$BakeDonut], event = $event")
    }

    def bake: Receive = {
      case BakeVanilla =>
        println("baking vanilla")

      case BakePlain =>
        println("baking plain")

      case StopBaking =>
        println("stopping to bake ")
        unbecome()

      case event @ _ =>
        println(s"Allowed event [$BakeVanilla, $BakePlain, $StopBaking], event = $event")
    }

    def receive = startBaking
  }


  system.terminate()
}
