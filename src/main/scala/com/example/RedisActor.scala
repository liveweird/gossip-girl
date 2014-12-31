package com.example

import akka.actor.{Actor, ActorLogging, Props}
import com.redis._

class RedisActor extends Actor with ActorLogging {
  import RedisActor._
  lazy val clients = new RedisClientPool("localhost", 56379)

  def receive = {

    case SetMsg(key, value) => {
      clients.withClient {
        client => {
          log.info("Setting key [{}] to value [{}]", key, value)
          val result = client.set(key, value)
          log.info("Setting has ended: [{}]", result)
        }
      }
    }
    case GetMsg(key) => {
      clients.withClient {
        client => {
          log.info("Getting key's value")
          val toReturn = client.get(key)
          sender ! ReturnedValMsg(toReturn)
        }
      }
    }
  }
}

object RedisActor {
  val props = Props[RedisActor]
  case class SetMsg(key: String, value: String)
  case class GetMsg(key: String)
  case class ReturnedValMsg(value: Option[String])
}