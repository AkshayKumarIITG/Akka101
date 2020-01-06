package fsm

import akka.actor.{ActorSystem, LoggingFSM, Props}

object F04_FSM extends App {

  println("Step 1: Create ActorSystem")
  val system = ActorSystem("DonutActorFSM")

  println("\nStep 6: Create DonutBakingActor")
  val bakingActor = system.actorOf(Props[DonutBakingActor], "donut-baking-actor")




  println("\nStep 7: Send events to actor to switch states and process events")
  bakingActor ! AddTopping
  Thread.sleep(2000)



  bakingActor ! BakeDonut
  Thread.sleep(2000)



  println("\nStep 2: Defining Events")
  sealed trait BakingEvents
  final case object BakeDonut extends BakingEvents
  final case object AddTopping extends BakingEvents

  println("\nStep 3: Defining States ")
  sealed trait BakingStates
  case object Start extends BakingStates
  case object Stop extends BakingStates

  println("\nStep 4: Defining mutable data ")
  case class BakingData(qty: Int) {
    def addOneDonut = copy(qty + 1)
  }

  object BakingData {
    def initialQuantity = BakingData(0)
  }



  class DonutBakingActor extends LoggingFSM[BakingStates, BakingData] {

    startWith(Stop, BakingData.initialQuantity)

    when(Stop) {
      case Event(BakeDonut, _) =>
        println("Current state is [Stop], switching to [Start]")
        goto(Start).using(stateData.addOneDonut)

      case Event(AddTopping, _) =>
        println("Current state is [Stop], you first need to move to [BakeDonut]")
        stay
    }

    when(Start){
      case _ => throw new IllegalStateException("stop")
    }

    initialize()
  }

  system.terminate()
}
