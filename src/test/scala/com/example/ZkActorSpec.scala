package net.gebski.gossip_girl.zk

import net.gebski.gossip_girl.zk.ZkActor.{TechErrorResponseMsg, CreateNodeResponseMsg}
import org.apache.zookeeper.KeeperException

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

      val result = Await.result(futureVal, 10 seconds)
      result should be (ZkActor.CurrStateResponseMsg(true))
    }

    it("check whether non-existent path exists") {
      val zkActor = system.actorOf(ZkActor.props)
      val futureVal = zkActor ? ZkActor.PathExistsMsg("/sciezka")

      val result = Await.result(futureVal, 10 seconds)
      result should be (ZkActor.PathExistsResponseMsg("/sciezka", false))
    }

    it("properly creates a top-level ephemeral node without a parent") {
      val zkActor = system.actorOf(ZkActor.props)

      val createFut = zkActor ? ZkActor.CreateNodeMsg("/sciezka", "whatever")
      val createRes = Await.result(createFut, 10 seconds)

      val newPath = createRes match {
        case ZkActor.CreateNodeResponseMsg(p, "whatever", true) => {
          val futureVal = zkActor ? ZkActor.PathExistsMsg(p)

          val result = Await.result(futureVal, 10 seconds)
          result should be (ZkActor.PathExistsResponseMsg(p, true))
        }
        case _ => {
          fail()
        }
      }
    }

    it("gets existing ephemeral node contents") {
      val zkActor = system.actorOf(ZkActor.props)

      val createFut = zkActor ? ZkActor.CreateNodeMsg("/sciezka", "whatever")
      val createRes = Await.result(createFut, 10 seconds)

      val newPath = createRes match {
        case ZkActor.CreateNodeResponseMsg(p, "whatever", true) => {
          p
        }
        case _ => {
          fail()
        }
      }

      val getFut = zkActor ? ZkActor.GetNodeMsg(newPath)
      val getRes = Await.result(getFut, 10 seconds)
      getRes should be (ZkActor.GetNodeResponseMsg(newPath, "whatever"))
    }

    it("gets non-existing ephemeral node & fails") {
      val zkActor = system.actorOf(ZkActor.props)

      val getFut = zkActor ? ZkActor.GetNodeMsg("/iamnothereatall")
      val getRes = Await.result(getFut, 10 seconds)
      getRes should be (TechErrorResponseMsg(KeeperException.Code.NONODE.toString()))
    }

    it("can't create an ephemeral node when parent doesn't exist") {
      val zkActor = system.actorOf(ZkActor.props)

      val createFut = zkActor ? ZkActor.CreateNodeMsg("/niemamnie/sciezka", "whatever")
      val createRes = Await.result(createFut, 10 seconds)
      createRes should be (TechErrorResponseMsg(KeeperException.Code.NONODE.toString()))
    }

    it("can't create any kind of child for ephemeral node") {
      val zkActor = system.actorOf(ZkActor.props)

      val createFut = zkActor ? ZkActor.CreateNodeMsg("/sciezka", "whatever")
      val createRes = Await.result(createFut, 10 seconds)

      val newPath = createRes match {
        case ZkActor.CreateNodeResponseMsg(p, "whatever", true) => {
          val futureVal = zkActor ? ZkActor.PathExistsMsg(p)

          val result = Await.result(futureVal, 10 seconds)
          result should be (ZkActor.PathExistsResponseMsg(p, true))

          val createFut2 = zkActor ? ZkActor.CreateNodeMsg(p + "/dzieciak", "whatever")
          val createRes2 = Await.result(createFut2, 10 seconds)
          createRes2 should be (TechErrorResponseMsg(KeeperException.Code.NOCHILDRENFOREPHEMERALS.toString()))
        }
        case _ => {
          fail()
        }
      }
    }
  }
}
