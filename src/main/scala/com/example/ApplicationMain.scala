package net.gebski.gossip_girl

import akka.actor.ActorSystem

object ApplicationMain extends App {
  val system = ActorSystem("gossip-girl")
  val redisActor = system.actorOf(RedisActor.props, "redis")
  val zkActor = system.actorOf(ZkActor.props, "zk")

  system.awaitTermination()
}