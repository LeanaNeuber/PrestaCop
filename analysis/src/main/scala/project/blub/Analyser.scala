package project.blub

import java.io.File
import java.nio.charset.StandardCharsets

import com.amazonaws.auth.{AWSCredentials, AWSCredentialsProvider, PropertiesCredentials}
import com.amazonaws.services.kinesis.model.{GetRecordsRequest, GetShardIteratorResult, Record}
import com.amazonaws.services.kinesis.{AmazonKinesis, AmazonKinesisClientBuilder}
import com.amazonaws.services.s3.model.{GetObjectRequest, ListObjectsRequest}
import com.amazonaws.services.s3.{AmazonS3, AmazonS3ClientBuilder}
import play.api.libs.json._
import project.Message

import scala.jdk.CollectionConverters._



object Analyser {

  implicit val messageWrite = Json.format[Message]

  def main(args: Array[String]): Unit = {

    val region = "eu-central-1"
    val bucket = "prestacop"

    val credits = getCreds
    val S3Client = buildS3Client(region, credits)

    val listObjectsRequest = new ListObjectsRequest().
      withBucketName(bucket).
      withDelimiter("/")

    var objects = S3Client.listObjects(listObjectsRequest)

    do {
      objects.getObjectSummaries().forEach(object_summary => S3Client.getObject(
        new GetObjectRequest(bucket , object_summary.getKey()),
        new File(""+object_summary.getKey().split("/")(object_summary.getKey().split("/").length-1))
      ))
      objects = S3Client.listNextBatchOfObjects(objects);
    } while (objects.isTruncated())

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

  private def buildS3Client(region: String, credentialsProvider: AWSCredentialsProvider) = {
    val clientBuilder = AmazonS3ClientBuilder.standard()
    clientBuilder.setRegion(region)
    clientBuilder.setCredentials(credentialsProvider)
    clientBuilder.build
  }
}
