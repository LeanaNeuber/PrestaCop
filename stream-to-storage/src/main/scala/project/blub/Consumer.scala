package project.blub

import java.io.File
import java.nio.charset.StandardCharsets

import com.amazonaws.auth.{AWSCredentials, AWSCredentialsProvider, PropertiesCredentials}
import com.amazonaws.services.kinesis.model.{GetRecordsRequest, GetShardIteratorResult, Record}
import com.amazonaws.services.kinesis.{AmazonKinesis, AmazonKinesisClientBuilder}
import com.amazonaws.services.s3.{AmazonS3, AmazonS3ClientBuilder}
import play.api.libs.json._
import project.Message

import scala.jdk.CollectionConverters._



object Consumer {

  implicit val messageWrite = Json.format[Message]

  def main(args: Array[String]): Unit = {

    if (args.length < 3)
      println("Three arguments are required to start this application: the aws region, the stream, and the bucket name!")

    val region = args(0)
    val stream = args(1)
    val bucket = args(2)

    val creds = getCreds
    checkShard(stream, bucket, buildS3Client(region, creds),buildKinesisClient(region, creds), "shardId-000000000000")

  }

  def checkShard(stream: String, bucket: String, s3Client: AmazonS3, kinesisClient: AmazonKinesis, shardId: String): Unit = {
    val shardIterator = kinesisClient.getShardIterator(stream, shardId, "LATEST").getShardIterator
    loopPoll(bucket, s3Client, kinesisClient, shardIterator)
  }

  def loopPoll(bucket: String, s3Client: AmazonS3, kinesisClient: AmazonKinesis, shardIterator: String):Unit =  {

    val getRecordsRequest = new GetRecordsRequest
    getRecordsRequest.setShardIterator(shardIterator)
    getRecordsRequest.setLimit(25)

    val recordsResult = kinesisClient.getRecords(getRecordsRequest)

    processRecords(bucket, s3Client, recordsResult.getRecords.asScala.toList)

    //Sleep for 1 seconds, then call the method again. Maybe not the best approach because never ending..
    Thread.sleep(1000)

    val next_iter = recordsResult.getNextShardIterator
    loopPoll(bucket, s3Client,kinesisClient, next_iter)

  }

  def processRecords(bucket: String, s3Client: AmazonS3, records: List[Record]) = {
    records.map(r => StandardCharsets.UTF_8.decode(r.getData).toString()).map(message => Json.parse(message).as[Message])
      .foreach(m => store(bucket, s3Client, m))
  }

  def store(bucket: String, s3Client: AmazonS3, message: Message): Unit = {
    s3Client.putObject(bucket, message.droneId +"_"+ message.time.replace("/", "-"), message.toString)
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

  private def buildKinesisClient(region: String, credentialsProvider: AWSCredentialsProvider) = {
    val clientBuilder = AmazonKinesisClientBuilder.standard()
    clientBuilder.setRegion(region)
    clientBuilder.setCredentials(credentialsProvider)
    clientBuilder.build
  }

  private def buildS3Client(region: String, credentialsProvider: AWSCredentialsProvider) = {
    val clientBuilder = AmazonS3ClientBuilder.standard()
    clientBuilder.setRegion(region)
    clientBuilder.setCredentials(credentialsProvider)
    clientBuilder.build
  }
}
