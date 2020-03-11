name := "CSV to kafka stream project"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies += "com.typesafe.play" %% "play-json" % "2.7.4"
libraryDependencies += "org.apache.spark" %% "spark-core" % "2.3.0"
libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.3.0"
libraryDependencies += "com.fasterxml.jackson.core" % "jackson-databind" % "2.6.5"
libraryDependencies += "org.twitter4j" % "twitter4j-core" % "4.0.5"
libraryDependencies += "com.google.code.gson" % "gson" % "2.7"
libraryDependencies += "org.apache.kafka" % "kafka-clients" % "2.4.0"
libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.6"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.3" % Runtime


