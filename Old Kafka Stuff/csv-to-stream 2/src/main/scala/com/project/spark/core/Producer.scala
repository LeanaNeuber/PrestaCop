package com.project.spark.core

import java.util.Properties

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import play.api.libs.json._

import scala.io.Source

object Producer {

  implicit val residentWrites: OWrites[Message] = Json.writes[Message]

  def main(args: Array[String]): Unit = {

    val pathToFile = "data/Parking_Violations_Issued_-_Fiscal_Year_2017.csv"

    val props = new Properties()
    props.put("bootstrap.servers", "localhost:9092")
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

    val producer = new KafkaProducer[String, String](props)

    extractCSV(pathToFile, producer)

    producer.close()
  }

  private def extractCSV(pathToFile: String, producer: KafkaProducer[String, String]) = {
    val bufferedSource = Source.fromFile(pathToFile)

    val header = bufferedSource.getLines.next().split(",").toList

    bufferedSource
      .getLines()
      .drop(1)
      .foreach(line => extractLines(producer, header, line.split(",").toList))

    bufferedSource.close()
  }

  //map to schema (case 48 columns do smth, case 51 do something, case else drop the line ) --> case class wtf?
  private def extractLines(producer: KafkaProducer[String, String], header: List[String], lineList: List[String]) = lineList match {
      case x if lineList.size > header.indexOf("Street Name") => {
        val location = x(header.indexOf("House Number")) + " " + x(header.indexOf("Street Name"))
        val time = x(header.indexOf("Issue Date"))
        val violationCode = x(header.indexOf("Violation Code"))
        writeToKafka(producer, "test", Message(location, time, "123droneID", Some(violationCode)))
      }
      case _ =>
  }

  def writeToKafka(producer: KafkaProducer[String, String], topic: String, message: Message): Unit = {
    val record = new ProducerRecord[String, String](topic, Json.toJson(message).toString())
    producer.send(record)
  }
}
