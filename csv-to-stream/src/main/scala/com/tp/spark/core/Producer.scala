package com.tp.spark.core

import java.util.Properties

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}

import scala.io.Source

object Producer {

  def main(args: Array[String]): Unit = {
    writeToKafka("test")
  }
  def writeToKafka(topic: String): Unit = {
    val props = new Properties()
    props.put("bootstrap.servers", "localhost:9092")
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    val producer = new KafkaProducer[String, String](props)
    val filename = "fileopen.scala"
    // use key to signal a violation!
    producer.send(new ProducerRecord[String, String](topic, "hello"))
    /*Source
      .fromFile(filename)
      .getLines
      .foreach(line => producer.send(new ProducerRecord[String, String](topic, line)))
*/
    producer.close()
  }



}
