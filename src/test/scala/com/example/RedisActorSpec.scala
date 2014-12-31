package com.example

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, FunSpecLike, Matchers}

class RedisActorSpec(_system: ActorSystem) extends TestKit(_system) with ImplicitSender with FunSpecLike with Matchers with BeforeAndAfterAll {

  def this() = this(ActorSystem("redis"))

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  describe("Redis actor") {
    it("setting a value works") {
      val redisActor = system.actorOf(RedisActor.props)
      redisActor ! RedisActor.Set("klucz", "wartosc")
      expectNoMsg()
    }
  }
}
