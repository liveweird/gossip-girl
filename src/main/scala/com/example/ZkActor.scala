package net.gebski.gossip_girl.zk

import com.twitter.util.{Await, JavaTimer, Future}
import com.twitter.zk.ZNode.Exists
import com.twitter.zk.{ZNode, NativeConnector, Connector, ZkClient}

import akka.actor.{ActorLogging, Actor, Props}
import org.apache.zookeeper.{ZooDefs, CreateMode}
import org.apache.zookeeper.ZooKeeper.States
import org.apache.zookeeper.data.{ACL, Stat}

class ZkActor extends Actor with ActorLogging {
  import ZkActor._
  import com.twitter.conversions.time._

  implicit val javaTimer = new JavaTimer(true)
  def zkClient = ZkClient("localhost:52181", Option(5 seconds), 5 seconds)

  override def receive = {
    case CurrStateMsg => {
      log.info("Checking client connection state")
      val zoo = Await.result(zkClient(), 2 seconds)
      val state = zoo.getState
      log.info("Client connection state: {}", state)
      sender ! CurrStateResponseMsg(state == States.CONNECTED)
    }
    case PathExistsMsg(path) => {
      log.info("Checking whether path [{}] exists", path)
      val zoo = Await.result(zkClient(), 2 seconds)
      val exists = zoo.exists(path, false)
      exists match {
        case null => sender ! PathExistsResponseMsg(path, false)
        case p: Stat => sender ! PathExistsResponseMsg(path, true)
      }
    }
    case CreateNodeMsg(path, content) => {
      log.info("Creating node in path [{}]", path)
      val zoo = Await.result(zkClient(), 2 seconds)
      val result = zoo.create(path, content.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL)
      log.info("Creating node in path [{}], result: [{}]", path, result)
      sender ! CreateNodeResponseMsg(path, content, true)
    }
  }
}

object ZkActor {
  val props = Props[ZkActor]
  object CurrStateMsg
  case class CurrStateResponseMsg(state: Boolean)
  case class PathExistsMsg(path: String)
  case class PathExistsResponseMsg(path: String, doesIt: Boolean)
  case class CreateNodeMsg(path: String, content: String)
  case class CreateNodeResponseMsg(path: String, content: String, created: Boolean)
}