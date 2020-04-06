name := "CSV to Stream"

version := "1.0"


import sbt._
import Keys._


scalaVersion := "2.13.1"

libraryDependencies += "com.typesafe.play" %% "play-json" % "2.7.4"
libraryDependencies += "com.amazonaws" % "aws-java-sdk" % "1.11.749"
libraryDependencies += "com.fasterxml.jackson.dataformat" % "jackson-dataformat-cbor" % "2.9.8"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.3" % Runtime

