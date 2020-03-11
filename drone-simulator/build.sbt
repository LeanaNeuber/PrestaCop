name := "Spark Project"

version := "1.0"


scalaVersion := "2.11.8"


libraryDependencies += "com.fasterxml.jackson.core" % "jackson-databind" % "2.6.5"
libraryDependencies += "com.google.code.gson" % "gson" % "2.7"
libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.6"
libraryDependencies += "org.apache.kafka" %% "kafka" % "2.1.0"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.3" % Runtime