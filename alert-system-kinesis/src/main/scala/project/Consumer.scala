package project

import java.io.File
import java.nio.charset.StandardCharsets

import com.amazonaws.auth.{AWSCredentials, AWSCredentialsProvider, PropertiesCredentials}
import com.amazonaws.services.kinesis.model.{GetRecordsRequest, GetShardIteratorResult, Record}
import com.amazonaws.services.kinesis.{AmazonKinesis, AmazonKinesisClientBuilder}
import play.api.libs.json._

import scala.jdk.CollectionConverters._



object Consumer {

  implicit val messageWrite = Json.format[Message]

  def main(args: Array[String]): Unit = {

    checkShard(buildKinesisClient(getCreds), "shardId-000000000000")

  }

  def checkShard(kinesisClient: AmazonKinesis, shardId: String): Unit = {
    val shardIterator = kinesisClient.getShardIterator("prestacop", shardId, "LATEST")
    loopPoll(kinesisClient, shardIterator)
  }

  def loopPoll(kinesisClient: AmazonKinesis, shardIterator: GetShardIteratorResult):Unit =  {
    val next_iter = shardIterator.getShardIterator

    val getRecordsRequest = new GetRecordsRequest
    getRecordsRequest.setShardIterator(next_iter)
    getRecordsRequest.setLimit(25)

    val records = kinesisClient.getRecords(getRecordsRequest).getRecords
    processRecords(records.asScala.toList)

    //Sleep for 3 seconds, then call the method again. Maybe not the best approach because never ending..
    Thread.sleep(3000)

    loopPoll(kinesisClient, shardIterator)

  }

  def processRecords(records: List[Record]) = {
    records.map(r => StandardCharsets.UTF_8.decode(r.getData).toString()).map(message => Json.parse(message).as[Message])
      .filter(message => message.violationCode.get.equals("42"))
      .foreach(m => alert(m))
  }

  def alert(message: Message): Unit = {
    println("----------------------------------------------------------------")
    println("ALERT!!!! For drone " + message.toString)
    println("----------------------------------------------------------------")
  }

  private def getCreds = {
    new AWSCredentialsProvider {
      override def refresh(): Unit = {}

      override def getCredentials: AWSCredentials = {
        val credentialsFile = new File(sys.env("HOME"), ".aws.properties")

        new PropertiesCredentials(credentialsFile)
      }
    }
  }

  private def buildKinesisClient(credentialsProvider: AWSCredentialsProvider) = {
    val clientBuilder = AmazonKinesisClientBuilder.standard()
    clientBuilder.setRegion("eu-central-1")
    clientBuilder.setCredentials(credentialsProvider)
    clientBuilder.build
  }

}
