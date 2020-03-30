package project.blub

import java.io.File
import java.nio.charset.StandardCharsets

import com.amazonaws.auth.{AWSCredentials, AWSCredentialsProvider, PropertiesCredentials}
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.document.DynamoDB
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec
import com.amazonaws.services.dynamodbv2.model.ScanRequest
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
    val table = "prestacop"

    val credits = getCreds

    val DBCLient = buildDynamoDBClient(region, table, credits)
    val scanRequest = new ScanRequest()
      .withTableName(table);
    val result = DBCLient.scan(scanRequest)
    print(result)
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

  private def getDynamoTable(region: String, table: String, credentialsProvider: AWSCredentialsProvider) = {
    val clientBuilder = AmazonDynamoDBClientBuilder.standard()
    clientBuilder.setRegion(region)
    clientBuilder.setCredentials(credentialsProvider)
    val client = clientBuilder.build

    val dynamoDB = new DynamoDB(client)
    dynamoDB.getTable(table)
  }

  private def buildDynamoDBClient(region: String, table: String, credentialsProvider: AWSCredentialsProvider) = {
    val clientBuilder = AmazonDynamoDBClientBuilder.standard()
    clientBuilder.setRegion(region)
    clientBuilder.setCredentials(credentialsProvider)
    clientBuilder.build
  }
}
