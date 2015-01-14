name := "gossip-girl"

version := "1.0"

scalaVersion := "2.11.4"

resolvers += "Twitter" at "http://maven.twttr.com/"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.3.8",
  "com.typesafe.akka" %% "akka-testkit" % "2.3.8" % "test",
  "org.scalatest" %% "scalatest" % "2.2.3" % "test",
  "net.debasishg" %% "redisclient" % "2.14",
  "org.slf4j" % "slf4j-simple" % "1.7.10",
  "com.twitter" % "util-zk_2.11" % "6.23.0")
