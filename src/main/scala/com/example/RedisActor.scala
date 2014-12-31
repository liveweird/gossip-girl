package com.example

import akka.actor.{Actor, ActorLogging, Props}
import com.redis._

class RedisActor extends Actor with ActorLogging {
  import RedisActor._
  val client = new RedisClient("localhost", 6379)

  def receive = {

    case Set(key, value) => {
      client.set(key, value)
    }
    case Get(key) => {
      val toReturn = client.get(key)
      sender ! ReturnValue(toReturn)
    }
  }
}

object RedisActor {
  val props = Props[RedisActor]
  case class Set(key: String, value: String)
  case class Get(key: String)
  case class ReturnValue(value: Option[String])
}