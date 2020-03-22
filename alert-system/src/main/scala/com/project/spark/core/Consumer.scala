package com.project.spark.core

import java.util.Properties

import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}
import org.apache.kafka.streams.scala.kstream.Consumed
import org.apache.kafka.streams.scala.{Serdes, StreamsBuilder}
import play.api.libs.json._

object Consumer {

  implicit val messageWrite = Json.format[Message]

  val props = new Properties()
  props.put("bootstrap.servers", "localhost:9092")
  props.put(StreamsConfig.APPLICATION_ID_CONFIG, "alert-application")

  def main(args: Array[String]): Unit = {
    val builder = new StreamsBuilder
    val messages = builder.stream("test")(Consumed.`with`(Serdes.String, Serdes.String))

    messages
      .mapValues(m => Json.parse(m).as[Message])
      .filter((k,v) => v.violationCode.get.equals("42"))
      .foreach((key,value) => alert(value))

    builder.build()

    val streams = new KafkaStreams(builder.build(), props)
    streams.start()

    sys.ShutdownHookThread {
      streams.close()
    }
  }

  def alert(message: Message): Unit = {
    println("----------------------------------------------------------------")
    println("ALERT!!!! For drone " + message.toString)
    println("----------------------------------------------------------------")
  }


}
