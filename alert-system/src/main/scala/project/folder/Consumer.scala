package project.folder

import java.io.File
import java.nio.charset.StandardCharsets

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.document.Item
import com.amazonaws.services.dynamodbv2.document.DynamoDB
import com.amazonaws.services.dynamodbv2.document.Table
import com.amazonaws.services.kinesis.model.{GetRecordsRequest, Record}
import com.amazonaws.services.kinesis.{AmazonKinesis, AmazonKinesisClientBuilder}
import com.amazonaws.services.sns.{AmazonSNS, AmazonSNSClientBuilder}
import com.amazonaws.services.sns.model.PublishRequest
import play.api.libs.json._

import scala.jdk.CollectionConverters._



object Consumer {

  implicit val messageWrite = Json.format[Message]

  def main(args: Array[String]): Unit = {
    if (args.length < 3)
      println("Three arguments are required to start this application: the aws region, the stream, and the sns topic name!")

    val region = args(0)
    val stream = args(1)
    val topicArn = args(2)

    checkShard(buildSnsClient(region), topicArn, stream, buildKinesisClient(region), "shardId-000000000000")
  }

  def checkShard(snsClient: AmazonSNS, topicArn: String, stream: String, kinesisClient: AmazonKinesis, shardId: String): Unit = {
    val shardIterator = kinesisClient.getShardIterator(stream, shardId, "LATEST").getShardIterator
    loopPoll(snsClient, topicArn, kinesisClient, shardIterator)
  }

  def loopPoll( snsClient: AmazonSNS, topicArn: String, kinesisClient: AmazonKinesis, shardIterator: String):Unit =  {

    val getRecordsRequest = new GetRecordsRequest
    getRecordsRequest.setShardIterator(shardIterator)
    getRecordsRequest.setLimit(25)

    val recordsResult = kinesisClient.getRecords(getRecordsRequest)
    processRecords(snsClient, topicArn,recordsResult.getRecords.asScala.toList)

    //Sleep for 1 seconds, then call the method again. Maybe not the best approach because never ending..
    Thread.sleep(1000)

    val next_iter = recordsResult.getNextShardIterator
    loopPoll(snsClient, topicArn, kinesisClient, next_iter)

  }

  // Raises an alarm if a message contains violationCode 666!
  def processRecords(snsClient: AmazonSNS, topicArn: String, records: List[Record]) = {
    records.map(r => StandardCharsets.UTF_8.decode(r.getData).toString()).map(message => Json.parse(message).as[Message])
      .filter(message => message.violationCode.get.equals("666"))
      .foreach(m => sendToSns(snsClient, topicArn, m))
  }

  def sendToSns(snsClient: AmazonSNS, topicArn: String, message: Message): Unit = {

    snsClient.publish(new PublishRequest(topicArn, message.toString))

    println("----------------------------------------------------------------")
    println("ALERT!!!! For drone " + message.toString)
    println("----------------------------------------------------------------")
  }

  private def buildKinesisClient(region: String) = {
    val clientBuilder = AmazonKinesisClientBuilder.standard()
    clientBuilder.setRegion(region)
    clientBuilder.build
  }

  private def buildSnsClient(region: String): AmazonSNS = {
    val clientBuilder = AmazonSNSClientBuilder.standard()
    clientBuilder.setRegion(region)
    clientBuilder.build
  }
}
