package project

import java.util.Properties

import org.apache.kafka.clients.producer._
import play.api.libs.json._

object DroneSim {
  implicit val residentWrites: OWrites[Message] = Json.writes[Message]

  def main(args: Array[String]): Unit = {
    println("Sending test message")

    val props = new Properties()
    props.put("bootstrap.servers", "localhost:9092")
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

    val producer = new KafkaProducer[String, String](props)
    val topic = "test"

    writeToKafka(producer, topic, Message("94800-Villejuif", "12:00", "100"))
    writeToKafka(producer, topic, Message("94800-Villejuif", "12:05", "102", Some("99"), Some("5")))

    producer.close()
    println("Transmission over")
  }

  def writeToKafka(producer: KafkaProducer[String, String], topic: String, message: Message): Unit = {
    val record = new ProducerRecord[String, String](topic, Json.toJson(message).toString())

    producer.send(record)
  }

}