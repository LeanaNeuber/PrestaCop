name := "Analysis service"

version := "1.0"


scalaVersion := "2.11.8"

libraryDependencies += "com.typesafe.play" %% "play-json" % "2.7.4"
libraryDependencies += "com.amazonaws" % "aws-java-sdk" % "1.11.648"

libraryDependencies += "com.fasterxml.jackson.core" % "jackson-databind" % "2.6.5"
libraryDependencies += "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.10.3"


libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.3" % Runtime

libraryDependencies += "org.apache.spark" %% "spark-core" % "2.3.0"
libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.3.0"
libraryDependencies += "org.apache.hadoop" % "hadoop-aws" % "3.2.1"
libraryDependencies += "com.amazon.emr" % "emr-dynamodb-hadoop" % "4.12.0"
libraryDependencies += "io.netty" % "netty-all" % "4.1.17.Final"
libraryDependencies += "io.netty" % "netty" % "3.9.9.Final"

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}

