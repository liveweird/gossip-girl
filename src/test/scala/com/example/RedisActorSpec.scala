package net.gebski.gossip_girl.redis

import scala.concurrent.duration._
import scala.concurrent.Await

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.testkit.{ImplicitSender, TestKit}
import akka.util.Timeout

import org.scalatest.{BeforeAndAfterAll, FunSpecLike, Matchers}

class RedisActorSpec(_system: ActorSystem) extends TestKit(_system) with ImplicitSender with FunSpecLike with Matchers with BeforeAndAfterAll {

  implicit val timeout = Timeout(5 seconds)
  def this() = this(ActorSystem("redis"))

  override def beforeAll() {
  }

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  describe("Redis actor") {

    it("check whether non-existent field exists") {
      val redisActor = system.actorOf(RedisActor.props)
      val futureVal = redisActor ? RedisActor.FieldExistsMsg("klucz", "pole")

      val result = Await.result(futureVal, 500 millis)
      result should be (RedisActor.FieldExistsResponseMsg("klucz", "pole", false))
    }

    it("try to get non-existent value") {
      val redisActor = system.actorOf(RedisActor.props)
      val futureVal = redisActor ? RedisActor.GetFieldMsg("klucz", "pole")

      val result = Await.result(futureVal, 500 millis)
      result should be (RedisActor.GetFieldResponseMsg("klucz", "pole", None))
    }

    it("setting a value works") {
      val redisActor = system.actorOf(RedisActor.props)
      redisActor ! RedisActor.SetFieldMsg("klucz", "pole", "wartosc")
      val futureVal = redisActor ? RedisActor.GetFieldMsg("kxlucz", "pole")

      val result = Await.result(futureVal, 500 millis)
      result should be (RedisActor.GetFieldResponseMsg("klucz", "pole", Some("wartosc")))

      redisActor ! RedisActor.DelFieldMsg("klucz", "pole")
    }
  }
}
