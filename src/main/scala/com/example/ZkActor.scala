package net.gebski.gossip_girl.zk

import com.twitter.util.{Await, JavaTimer, Future}
import com.twitter.zk.{NativeConnector, Connector, ZkClient}

import akka.actor.{ActorLogging, Actor, Props}
import org.apache.zookeeper.ZooKeeper.States

class ZkActor extends Actor with ActorLogging {
  import ZkActor._
  import com.twitter.conversions.time._

  implicit val javaTimer = new JavaTimer(true)
  def zkClient = ZkClient("localhost:2181", Option(5 seconds), 5 seconds)

  override def receive = {
    case CurrStateMsg => {
      log.info("Checking client connection state")
      val zoo = Await.result(zkClient())
      val state = zoo.getState
      log.info("Client connection state: {}", state)
      sender ! CurrStateResponseMsg(state == States.CONNECTED)
    }
    case PathExistsMsg(path) => {
      log.info("Checking whether path [{}] exists", path)
      val zoo = Await.result(zkClient())
      // zoo.get
      // val result = Await.result(znode.exists(), 5.seconds)
      sender ! PathExistsResponseMsg(path, false)
    }
  }
}

object ZkActor {
  val props = Props[ZkActor]
  object CurrStateMsg
  case class CurrStateResponseMsg(state: Boolean)
  case class PathExistsMsg(path: String)
  case class PathExistsResponseMsg(path: String, doesIt: Boolean)
}