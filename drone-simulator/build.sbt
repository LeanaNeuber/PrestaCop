name := "Drone Simulator"

version := "1.0"


import sbt._
import Keys._


scalaVersion := "2.11.8"

libraryDependencies += "com.typesafe.play" %% "play-json" % "2.7.4"
libraryDependencies += "org.apache.kafka" %% "kafka" % "2.4.0"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.3" % Runtime
