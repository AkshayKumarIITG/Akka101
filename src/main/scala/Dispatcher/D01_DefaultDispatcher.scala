package Dispatcher

import akka.actor.ActorSystem

object D01_DefaultDispatcher extends App {

  println("Step 1: Create an actor system")
  val system = ActorSystem("DonutStoreActorSystem")


  println("\nStep 2: Akka default dispatcher config")
  val defaultDispatcherConfig = system.settings.config.getConfig("akka.actor.default-dispatcher")
  println(s"akka.actor.default-dispatcher = $defaultDispatcherConfig")



  println("\nStep 3: Akka default dispatcher type")
  val dispatcherType = defaultDispatcherConfig.getString("type")
  println(s"$dispatcherType")



  println("\nStep 4: Akka default dispatcher throughput")
  val dispatcherThroughput = defaultDispatcherConfig.getString("throughput")
  println(s"$dispatcherThroughput")



  println("\nStep 5: Akka default dispatcher minimum parallelism")
  val dispatcherParallelismMin = defaultDispatcherConfig.getInt("fork-join-executor.parallelism-min")
  println(s"$dispatcherParallelismMin")


  println("\nStep 6: Akka default dispatcher maximum parallelism")
  val dispatcherParallelismMax = defaultDispatcherConfig.getInt("fork-join-executor.parallelism-max")
  println(s"$dispatcherParallelismMax")



  println("\nStep 7: Close the actor system")
  val isTerminated = system.terminate()

}
