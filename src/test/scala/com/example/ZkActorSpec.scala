package net.gebski.gossip_girl.zk

import net.gebski.gossip_girl.zk.ZkActor.CreateNodeResponseMsg

import scala.concurrent.Await

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.testkit.{ImplicitSender, TestKit}

import org.scalatest.{BeforeAndAfterAll, Matchers, FunSpecLike}

import scala.concurrent.duration._

class ZkActorSpec(_system: ActorSystem) extends TestKit(_system) with ImplicitSender with FunSpecLike with Matchers with BeforeAndAfterAll {

  implicit val timeout = akka.util.Timeout(5 seconds)
  def this() = this(ActorSystem("zk"))

  override def beforeAll() {
  }

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  describe("ZooKeeper actor") {

    it("connection is fine") {
      val zkActor = system.actorOf(ZkActor.props)
      val futureVal = zkActor ? ZkActor.CurrStateMsg

      val result = Await.result(futureVal, 2 seconds)
      result should be (ZkActor.CurrStateResponseMsg(true))
    }

    it("check whether non-existent path exists") {
      val zkActor = system.actorOf(ZkActor.props)
      val futureVal = zkActor ? ZkActor.PathExistsMsg("/sciezka")

      val result = Await.result(futureVal, 2 seconds)
      result should be (ZkActor.PathExistsResponseMsg("/sciezka", false))
    }

    it("properly creates a top-level ephemeral node without a parent") {
      val zkActor = system.actorOf(ZkActor.props)

      val createFut = zkActor ? ZkActor.CreateNodeMsg("/sciezka", "whatever")
      val createRes = Await.result(createFut, 2 seconds)

      val newPath = createRes match {
        case ZkActor.CreateNodeResponseMsg(p, "whatever", true) => {
          p
        }
        case _ => {
          fail()
        }
      }

      val getFut = zkActor ? ZkActor.GetNodeMsg(newPath)
      val getRes = Await.result(getFut, 2 seconds)
      getRes should be (ZkActor.GetNodeResponseMsg(newPath, "whatever"))
    }
  }
}
