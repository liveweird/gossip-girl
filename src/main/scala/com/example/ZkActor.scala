package net.gebski.gossip_girl.zk

import akka.actor.{ActorLogging, Actor, Props}

class ZkActor extends Actor with ActorLogging {
  import ZkActor._

  override def receive = {
    case PathExistsMsg(path) => {
      log.info("Checking whether path [{}] exists", path)
      sender ! PathExistsResponseMsg(path, false)
    }
  }
}

object ZkActor {
  val props = Props[ZkActor]
  case class PathExistsMsg(path: String)
  case class PathExistsResponseMsg(path: String, doesIt: Boolean)
}