package net.gebski.gossip_girl

import akka.actor.ActorSystem
import net.gebski.gossip_girl.redis.RedisActor
import net.gebski.gossip_girl.zk.ZkActor

object ApplicationMain extends App {
  val system = ActorSystem("gossip-girl")
  val redisActor = system.actorOf(RedisActor.props, "redis")
  val zkActor = system.actorOf(ZkActor.props, "zk")

  system.awaitTermination()
}