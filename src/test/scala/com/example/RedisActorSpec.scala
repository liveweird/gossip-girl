package com.example

import akka.util.Timeout

import scala.concurrent.duration._
import scala.concurrent.Await

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, FunSpecLike, Matchers, TryValues}

import scala.util.{Failure, Success}

class RedisActorSpec(_system: ActorSystem) extends TestKit(_system) with ImplicitSender with FunSpecLike with Matchers with BeforeAndAfterAll {

  implicit val timeout = Timeout(5 seconds)
  def this() = this(ActorSystem("redis"))

  override def beforeAll() {
  }

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  describe("Redis actor") {

    it("check whether non-existent key exists") {
      val redisActor = system.actorOf(RedisActor.props)
      val futureVal = redisActor ? RedisActor.KeyExistsMsg("klucz")

      val result = Await.result(futureVal, 500 millis)
      result should be (Success("klucz", false))
    }

    it("try to get inexistent value") {
      val redisActor = system.actorOf(RedisActor.props)
      val futureVal = redisActor ? RedisActor.GetMsg("klucz")

      val result = Await.result(futureVal, 500 millis)
      result should be (Failure)
    }

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
