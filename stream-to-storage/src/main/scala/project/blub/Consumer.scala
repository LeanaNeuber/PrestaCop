package project.blub

import java.io.File
import java.nio.charset.StandardCharsets

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.document.{DynamoDB, Item, Table}
import com.amazonaws.services.kinesis.model.{GetRecordsRequest, Record}
import com.amazonaws.services.kinesis.{AmazonKinesis, AmazonKinesisClientBuilder}
import play.api.libs.json._
import project.Message

import scala.jdk.CollectionConverters._



object Consumer {

  implicit val messageWrite = Json.format[Message]

  def main(args: Array[String]): Unit = {

    if (args.length < 3)
      println("Three arguments are required to start this application: the aws region, the stream, and the DynamoDb table!")

    val region = args(0)
    val stream = args(1)
    val table = args(2)

    checkShard(stream, getDynamoTable(region, table),buildKinesisClient(region), "shardId-000000000000")

  }

  def checkShard(stream: String, table: Table, kinesisClient: AmazonKinesis, shardId: String): Unit = {
    val shardIterator = kinesisClient.getShardIterator(stream, shardId, "LATEST").getShardIterator
    loopPoll(table, kinesisClient, shardIterator)
  }

  @scala.annotation.tailrec
  def loopPoll(table: Table, kinesisClient: AmazonKinesis, shardIterator: String):Unit =  {

    val getRecordsRequest = new GetRecordsRequest
    getRecordsRequest.setShardIterator(shardIterator)
    getRecordsRequest.setLimit(25)

    val recordsResult = kinesisClient.getRecords(getRecordsRequest)

    processRecords(table, recordsResult.getRecords.asScala.toList)

    //Sleep for 1 seconds, then call the method again. Maybe not the best approach because never ending..
    Thread.sleep(1000)

    val next_iter = recordsResult.getNextShardIterator
    loopPoll(table,kinesisClient, next_iter)
  }

  def processRecords(table: Table, records: List[Record]) = {
    records.map(r => StandardCharsets.UTF_8.decode(r.getData).toString()).map(message => Json.parse(message).as[Message])
      .foreach(m => storeToDynamo(table, m))
  }

  def storeToDynamo(table: Table, message: Message): Unit = {
    val item = new Item().withPrimaryKey("id", message.droneId+"_"+message.time)
      .withString("location",message.location)
      .withString("time",message.time)
      .withString("droneId", message.droneId)
      //Add a TTL of 5 days from now
      .withLong("ttl", System.currentTimeMillis() / 1000L + 432000L  )
    if(message.violationCode.isDefined && message.violationImageId.isDefined)
      item.withString("violationCode",message.violationCode.getOrElse(null))
        .withString("violationImageId",message.violationImageId.getOrElse(null))
    table.putItem(item)
    println("Successfully stored message: " + message.toString)
  }

  private def buildKinesisClient(region: String): AmazonKinesis = {
    val clientBuilder = AmazonKinesisClientBuilder.standard()
    clientBuilder.setRegion(region)
    val client = clientBuilder.build
    println("Successfully built the Kinesis client!")
    client
  }

  private def getDynamoTable(region: String, table_name: String) = {
    val clientBuilder = AmazonDynamoDBClientBuilder.standard()
    clientBuilder.setRegion(region)
    val client = clientBuilder.build

    val dynamoDB = new DynamoDB(client)
    val table = dynamoDB.getTable(table_name)
    println("Successfully built the DynamoDB client and retrieved the table!")
    table
  }

}
