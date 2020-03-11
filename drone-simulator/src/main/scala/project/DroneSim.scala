package project

import java.util.Properties
import org.apache.kafka.clients.producer._

object DroneSim {
  def main(args: Array[String]): Unit = {
    println("Sending test message")

    val props = new Properties()
    props.put("bootstrap.servers", "localhost:9092")
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

    val producer = new KafkaProducer[String, String](props)
    val topic = "test"

    writeToKafka(producer, topic, "drone-simulation-message", "first message")
    writeToKafka(producer, topic, "drone-simulation-message", "second message")
    writeToKafka(producer, topic, "drone-simulation-message", "third message")
    writeToKafka(producer, topic, "drone-simulation-message", "fourth message")

    println("Transmission over")
  }
  def writeToKafka(producer: KafkaProducer[String, String], topic: String, key: String, message: String): Unit = {
    val record = new ProducerRecord[String, String](topic, "key", "coucou")

    producer.send(record)

    producer.close()

  }

}