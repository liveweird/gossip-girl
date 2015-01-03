package com.example

import scala.concurrent.duration._
import scala.concurrent.Await

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.testkit.{ImplicitSender, TestKit}
import akka.util.Timeout
import org.scalatest.{BeforeAndAfterAll, Matchers, FunSpecLike}

class ZkActorSpec(_system: ActorSystem) extends TestKit(_system) with ImplicitSender with FunSpecLike with Matchers with BeforeAndAfterAll {

  implicit val timeout = Timeout(5 seconds)
  def this() = this(ActorSystem("zk"))

  override def beforeAll() {
  }

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  describe("ZooKeeper actor") {
    it("check whether non-existent path exists") {
      val zkActor = system.actorOf(ZkActor.props)
      val futureVal = zkActor ? ZkActor.PathExistsMsg("/sciezka")

      val result = Await.result(futureVal, 500 millis)
      result should be (ZkActor.PathExistsResponseMsg("/sciezka", false))
    }
  }
}
