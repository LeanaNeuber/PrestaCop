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

    val r = scala.util.Random

    // send 100 messages
    // Violation Code: 666 raises an Alert (make sure it's not one that is raised by the NYPD dataset)
    // make every 10th message an alert (even though it says 1% in the original project description)
    (1 to 100).foreach(i => {
      sendMessage(kinesisClient, Message("94800-Villejuif", Calendar.getInstance().getTime.toString , i.toString, Some((666 + i%10).toString), Some(r.nextInt(100).toString)))
      Thread.sleep(1000)
    })
  }


  private def sendMessage(kinesisClient: AmazonKinesis, message: Message) = {
    println("Sending Message....")

    val putRecordRequest = new PutRecordRequest
    putRecordRequest.setStreamName("prestacop")
    putRecordRequest.setPartitionKey("key")
    putRecordRequest.setData(ByteBuffer.wrap(Json.toJson(message).toString().getBytes()))

    kinesisClient.putRecord(putRecordRequest)
  }

  private def buildKinesisClient(credentialsProvider: AWSCredentialsProvider) = {
    val clientBuilder = AmazonKinesisClientBuilder.standard()
    clientBuilder.setRegion("eu-central-1")
    clientBuilder.setCredentials(credentialsProvider)
    clientBuilder.build
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
}
