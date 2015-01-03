name := "gossip-girl"

version := "1.0"

scalaVersion := "2.11.4"

resolvers += "Twitter" at "http://maven.twttr.com/"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.3.8",
  "com.typesafe.akka" %% "akka-testkit" % "2.3.8" % "test",
  "org.scalatest" %% "scalatest" % "2.2.0" % "test",
  "net.debasishg" %% "redisclient" % "2.13",
  "org.slf4j" % "slf4j-simple" % "1.7.9",
  "com.twitter" % "util-zk" % "6.5.0")
