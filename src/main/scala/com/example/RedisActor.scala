package com.example

import akka.actor.{Actor, ActorLogging, Props}
import com.redis._

class RedisActor extends Actor with ActorLogging {
  import RedisActor._
  lazy val clients = new RedisClientPool("localhost", 56379)

  def receive = {

    case FieldExistsMsg(key, field) => {
      clients.withClient {
        client => {
          log.info("Checking whether key [{}], field [{}] exists", key, field)
          val result = client.hexists(key, field)
          sender ! FieldExistsResponseMsg(key, field, result)
        }
      }
    }
    case SetFieldMsg(key, field, value) => {
      clients.withClient {
        client => {
          log.info("Setting key [{}], field [{}] to value [{}]", key, field, value)
          val result = client.hset(key, field, value)
          log.info("Setting has ended: [{}]", result)
        }
      }
    }
    case GetFieldMsg(key, field) => {
      clients.withClient {
        client => {
          log.info("Getting key's value for key [{}], field [{}]", key, field)
          val toReturn = client.hget(key, field)
          sender ! GetFieldResponseMsg(key, field, toReturn)
        }
      }
    }
    case DelFieldMsg(key, field) => {
      clients.withClient {
        client => {
          log.info("Deleting key [{}], field [{}]", key, field)
          client.hdel(key, field)
        }
      }
    }
  }
}

object RedisActor {
  val props = Props[RedisActor]
  case class KeyExistsMsg(key: String)
  case class KeyExistsResponseMsg(key: String, doesIt: Boolean)
  case class FieldExistsMsg(key: String, field: String)
  case class FieldExistsResponseMsg(key: String, field: String, doesIt: Boolean)
  case class GetFieldMsg(key: String, field: String)
  case class GetFieldResponseMsg(key: String, field: String, value: Option[String])
  case class SetFieldMsg(key: String, field: String, value: String)
  case class DelFieldMsg(key: String, field: String)
}