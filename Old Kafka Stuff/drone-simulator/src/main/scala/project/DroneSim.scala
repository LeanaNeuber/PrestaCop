package project

import java.util.{Calendar, Properties}

import org.apache.kafka.clients.producer._
import play.api.libs.json._

object DroneSim {
  implicit val residentWrites: OWrites[Message] = Json.writes[Message]

  def main(args: Array[String]): Unit = {
    val props = new Properties()
    props.put("bootstrap.servers", "localhost:9092")
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

    val producer = new KafkaProducer[String, String](props)
    val topic = "test"
    val messageCount = 100

    // We need to mock messages and decide upon a violation code that triggers an alert... take 42
    // Should we try to send messages never ending in a time interval (how in a functional way?)
    // Now: Send 100 messages, every 3 seconds, every fifth is a violation

    //Is it even necessary to send non-alert messages? Because the messages are fake anyway and shouldn't be considered in our analysis

    val r = scala.util.Random

    (1 to messageCount).foreach(i => {
      writeToKafka(producer, topic, Message("94800-Villejuif", Calendar.getInstance().getTime.toString , r.nextInt(100).toString, Some((42 + i%5).toString), Some(r.nextInt(100).toString)))
      Thread.sleep(3000)
    })

    producer.close()
  }

  def writeToKafka(producer: KafkaProducer[String, String], topic: String, message: Message): Unit = {
    val record = new ProducerRecord[String, String](topic, Json.toJson(message).toString())
    producer.send(record)
  }

}