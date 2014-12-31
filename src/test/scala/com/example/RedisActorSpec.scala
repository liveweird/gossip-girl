package com.example

import akka.util.Timeout

import scala.concurrent.duration._
import scala.concurrent.Await

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, FunSpecLike, Matchers}

import scala.util.Success

class RedisActorSpec(_system: ActorSystem) extends TestKit(_system) with ImplicitSender with FunSpecLike with Matchers with BeforeAndAfterAll {

  implicit val timeout = Timeout(5 seconds)
  def this() = this(ActorSystem("redis"))

  override def beforeAll() {
  }

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  describe("Redis actor") {
    it("setting a value works") {
      val redisActor = system.actorOf(RedisActor.props)
      redisActor ! RedisActor.SetMsg("klucz", "wartosc")
      //redisActor ! RedisActor.GetMsg("klucz")
      //expectMsg(500 millis, RedisActor.ReturnedValMsg)

      val futureVal = redisActor ? RedisActor.GetMsg("klucz")
      val Success(result: RedisActor.ReturnedValMsg) =  futureVal.value.get
      result should be ("wartosc")
    }
  }
}
