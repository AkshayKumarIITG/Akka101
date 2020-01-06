package fsm

import akka.actor.ActorSystem
import akka.testkit.{DefaultTimeout, ImplicitSender, TestFSMRef, TestKit}
import fsm.F09_FSM_PartSix._
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

class DonutBakingActorFSMTests
    extends TestKit(ActorSystem("DonutActorFSM"))
      with ImplicitSender
      with DefaultTimeout
      with WordSpecLike
      with BeforeAndAfterAll
      with Matchers {

  private var donutBakingActorFSM: TestFSMRef[BakingStates, BakingData, DonutBakingActor] = _

  //A protected member is only accessible from subclasses of the class in which the member is defined

  override protected def beforeAll(): Unit = {
    donutBakingActorFSM = TestFSMRef(new DonutBakingActor())
  }

  "DonutBakingActor" should {
    "have initial state of BakingStates.Stop" in {
      donutBakingActorFSM.stateName shouldEqual Stop
    }
  }

  override protected def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

}
