package project.blub

import java.io.File
import java.nio.charset.StandardCharsets

import com.amazonaws.auth.{AWSCredentials, AWSCredentialsProvider, PropertiesCredentials}
import com.amazonaws.services.kinesis.model.{GetRecordsRequest, GetShardIteratorResult, Record}
import com.amazonaws.services.kinesis.{AmazonKinesis, AmazonKinesisClientBuilder}
import com.amazonaws.services.s3.model.PutObjectRequest
import com.amazonaws.services.s3.{AmazonS3, AmazonS3ClientBuilder}
import play.api.libs.json._
import project.Message

import scala.jdk.CollectionConverters._



object Consumer {

  implicit val messageWrite = Json.format[Message]

  def main(args: Array[String]): Unit = {
    val creds = getCreds
    checkShard(buildS3Client(creds),buildKinesisClient(creds), "shardId-000000000000")

  }

  def checkShard(s3Client: AmazonS3, kinesisClient: AmazonKinesis, shardId: String): Unit = {
    val shardIterator = kinesisClient.getShardIterator("prestacop", shardId, "LATEST")
    loopPoll(s3Client, kinesisClient, shardIterator)
  }

  def loopPoll(s3Client: AmazonS3, kinesisClient: AmazonKinesis, shardIterator: GetShardIteratorResult):Unit =  {
    val next_iter = shardIterator.getShardIterator

    val getRecordsRequest = new GetRecordsRequest
    getRecordsRequest.setShardIterator(next_iter)
    getRecordsRequest.setLimit(10)

    val records = kinesisClient.getRecords(getRecordsRequest).getRecords
    processRecords(s3Client, records.asScala.toList)

    //Sleep for 3 seconds, then call the method again. Maybe not the best approach because never ending..
    Thread.sleep(3000)

    loopPoll(s3Client,kinesisClient, shardIterator)

  }

  def processRecords(s3Client: AmazonS3, records: List[Record]) = {
    records.map(r => StandardCharsets.UTF_8.decode(r.getData).toString()).map(message => Json.parse(message).as[Message])
      .filter(message => message.violationCode.get.equals("42"))
      .foreach(m => store(s3Client, m))
  }

  def store(s3Client: AmazonS3, message: Message): Unit = {
    println("Store something!")
    s3Client.putObject("prestacop" + "/" + message.droneId, message.droneId + message.time, message.toString)
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

  private def buildKinesisClient(credentialsProvider: AWSCredentialsProvider) = {
    val clientBuilder = AmazonKinesisClientBuilder.standard()
    clientBuilder.setRegion("eu-central-1")
    clientBuilder.setCredentials(credentialsProvider)
    clientBuilder.build
  }

  private def buildS3Client(credentialsProvider: AWSCredentialsProvider) = {
    val clientBuilder = AmazonS3ClientBuilder.standard()
    clientBuilder.setRegion("eu-central-1")
    clientBuilder.setCredentials(credentialsProvider)
    clientBuilder.build
  }
}
