package project.folder

import java.io.File
import java.nio.charset.StandardCharsets

import com.amazonaws.auth.{AWSCredentials, AWSCredentialsProvider, PropertiesCredentials}
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.document.Item
import com.amazonaws.services.dynamodbv2.document.DynamoDB
import com.amazonaws.services.dynamodbv2.document.Table
import com.amazonaws.services.kinesis.model.{GetRecordsRequest, Record}
import com.amazonaws.services.kinesis.{AmazonKinesis, AmazonKinesisClientBuilder}
import play.api.libs.json._

import scala.jdk.CollectionConverters._



object Consumer {

  implicit val messageWrite = Json.format[Message]

  def main(args: Array[String]): Unit = {
    val creds = getCreds
    checkShard(getDynamoTable(creds), buildKinesisClient(creds), "shardId-000000000000")
  }

  def checkShard(table: Table, kinesisClient: AmazonKinesis, shardId: String): Unit = {
    val shardIterator = kinesisClient.getShardIterator("prestacop", shardId, "LATEST").getShardIterator
    loopPoll(table, kinesisClient, shardIterator)
  }

  def loopPoll(table: Table, kinesisClient: AmazonKinesis, shardIterator: String):Unit =  {

    val getRecordsRequest = new GetRecordsRequest
    getRecordsRequest.setShardIterator(shardIterator)
    getRecordsRequest.setLimit(25)

    val recordsResult = kinesisClient.getRecords(getRecordsRequest)
    processRecords(table, recordsResult.getRecords.asScala.toList)

    //Sleep for 1 seconds, then call the method again. Maybe not the best approach because never ending..
    Thread.sleep(1000)

    val next_iter = recordsResult.getNextShardIterator
    loopPoll(table, kinesisClient, next_iter)

  }

  // Raises an alarm if a message contains violationCode 666!
  def processRecords(table: Table, records: List[Record]) = {
    records.map(r => StandardCharsets.UTF_8.decode(r.getData).toString()).map(message => Json.parse(message).as[Message])
      .filter(message => message.violationCode.get.equals("666"))
      .foreach(m => storeToDynamo(table, m))
  }

  def storeToDynamo(table: Table, message: Message): Unit = {
    table.putItem(new Item()
      .withPrimaryKey("id", message.droneId+"_"+message.time)
      .withString("location",message.location)
      .withString("time",message.time)
      .withString("droneId", message.droneId)
      .withString("violationCode",message.violationCode.getOrElse(null))
      .withString("violationImageId",message.violationImageId.getOrElse(null)))


    println("----------------------------------------------------------------")
    println("ALERT!!!! For drone " + message.toString)
    println("----------------------------------------------------------------")
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

  private def getDynamoTable(credentialsProvider: AWSCredentialsProvider) = {
    val clientBuilder = AmazonDynamoDBClientBuilder.standard()
    clientBuilder.setRegion("eu-central-1")
    clientBuilder.setCredentials(credentialsProvider)
    val client = clientBuilder.build

    val dynamoDB = new DynamoDB(client)
    dynamoDB.getTable("prestacop")
  }

  private def buildKinesisClient(credentialsProvider: AWSCredentialsProvider) = {
    val clientBuilder = AmazonKinesisClientBuilder.standard()
    clientBuilder.setRegion("eu-central-1")
    clientBuilder.setCredentials(credentialsProvider)
    clientBuilder.build
  }
}
