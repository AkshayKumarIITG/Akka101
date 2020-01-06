package Dispatcher

import akka.actor.ActorSystem

object D02_AkkaLookupDispatcher extends App{

  val system = ActorSystem("DonutStoreActorSystem")
  implicit val executionContext = system.dispatchers.lookup("fixed-thread-pool")
  system.terminate()

}
