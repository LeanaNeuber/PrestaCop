package project

import java.io.File
import java.nio.ByteBuffer
import java.util.Calendar

import com.amazonaws.auth.{AWSCredentials, AWSCredentialsProvider, PropertiesCredentials}
import com.amazonaws.services.kinesis.model.PutRecordRequest
import com.amazonaws.services.kinesis.{AmazonKinesis, AmazonKinesisClient, AmazonKinesisClientBuilder, model}
import play.api.libs.json.{Json, OWrites}

object DroneSim {

  implicit val residentWrites: OWrites[Message] = Json.writes[Message]

  def main(args: Array[String]): Unit = {

    val credentialsProvider = new AWSCredentialsProvider {
      override def refresh(): Unit = {}

      override def getCredentials: AWSCredentials = {
        val credentialsFile = new File(sys.env("HOME"), ".aws.properties")

        new PropertiesCredentials(credentialsFile)
      }
    }

    val kinesisClient = buildKinesisClient(credentialsProvider)

    val messageCount = 100
    val r = scala.util.Random

    (1 to messageCount).foreach(i => {
      sendMessage(kinesisClient, Message("94800-Villejuif", Calendar.getInstance().getTime.toString , r.nextInt(100).toString, Some((42 + i%5).toString), Some(r.nextInt(100).toString)))
      Thread.sleep(3000)
    })
  }

  private def sendMessage(kinesisClient: AmazonKinesis, message: Message) = {
    // Think about using putRecordsRequest and send several at a time
    // This approach is sending an HTTP request for every message... not cool
    // Takes around 5 minutes like this for 100 messages
    val putRecordRequest = new PutRecordRequest
    putRecordRequest.setStreamName("prestacop")
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