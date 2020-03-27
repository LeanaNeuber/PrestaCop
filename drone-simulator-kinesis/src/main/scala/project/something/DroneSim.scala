package project.something

import java.io.File
import java.nio.ByteBuffer
import java.util.Calendar

import com.amazonaws.auth.{AWSCredentials, AWSCredentialsProvider, PropertiesCredentials}
import com.amazonaws.services.kinesis.model.PutRecordRequest
import com.amazonaws.services.kinesis.{AmazonKinesis, AmazonKinesisClientBuilder}
import play.api.libs.json.{Json, OWrites}
import project.Message

object DroneSim {

  implicit val residentWrites: OWrites[Message] = Json.writes[Message]

  def main(args: Array[String]): Unit = {

    val kinesisClient = buildKinesisClient(getCreds)

    val messageCount = 100
    val r = scala.util.Random

    (1 to messageCount).foreach(i => {
      sendMessage(kinesisClient, Message("94800-Villejuif", Calendar.getInstance().getTime.toString , r.nextInt(100).toString, Some((42 + i%5).toString), Some(r.nextInt(100).toString)))
      Thread.sleep(3000)
    })
  }

  private def getCreds = {
    new AWSCredentialsProvider {
      override def refresh(): Unit = {}

      override def getCredentials: AWSCredentials = {
        val credentialsFile = new File(System.getProperty("user.home"), ".aws.properties")

        new PropertiesCredentials(credentialsFile)
      }
    }
  }

  private def sendMessage(kinesisClient: AmazonKinesis, message: Message) = {
    // Think about using putRecordsRequest and send several at a time
    // This approach is sending an HTTP request for every message... not cool
    // Takes around 5 minutes like this for 100 messages
    println("Sending Message....")

    val putRecordRequest = new PutRecordRequest
    putRecordRequest.setStreamName("prestacop")
    // This I don't understand with the partitionkey
    putRecordRequest.setPartitionKey("somekey")
    putRecordRequest.setData(ByteBuffer.wrap(Json.toJson(message).toString().getBytes()))

    kinesisClient.putRecord(putRecordRequest)
  }

  private def buildKinesisClient(credentialsProvider: AWSCredentialsProvider) = {
    val clientBuilder = AmazonKinesisClientBuilder.standard()
    clientBuilder.setRegion("eu-central-1")
    clientBuilder.setCredentials(credentialsProvider)

    clientBuilder.build
  }
}
