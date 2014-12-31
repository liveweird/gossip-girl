name := "gossip-girl"

version := "1.0"

scalaVersion := "2.11.4"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.3.8" withJavadoc() withSources(),
  "com.typesafe.akka" %% "akka-testkit" % "2.3.8" % "test" withJavadoc() withSources(),
  "org.scalatest" %% "scalatest" % "2.2.0" % "test" withJavadoc() withSources())
