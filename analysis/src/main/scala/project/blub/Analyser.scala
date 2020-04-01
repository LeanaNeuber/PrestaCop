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
import com.amazonaws.services.simpledb.model.Item
import org.apache.hadoop.dynamodb.DynamoDBItemWritable
import org.apache.hadoop.mapred.JobConf
import play.api.libs.json._
import project.Message
import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.hadoop.dynamodb.read.DynamoDBInputFormat

import org.apache.hadoop.io.Text


object Analyser {

  implicit val messageWrite = Json.format[Message]

  def main(args: Array[String]): Unit = {

    val region = "eu-central-1"
    val table = "prestacop"
    val credits = getCreds
    val DBCLient = buildDynamoDBClient(region, table, credits)

    val conf = new SparkConf()
      .setAppName("Analyser")
      .setMaster("local[*]") // here local mode. And * means you will use as much as you have cores.

    val sc = SparkContext.getOrCreate(conf)

    var jobConf = new JobConf(sc.hadoopConfiguration)
    jobConf.set("dynamodb.servicename", "dynamodb")
    jobConf.set("dynamodb.input.tableName", table)
    jobConf.set("dynamodb.regionid", region)
    jobConf.set("dynamodb.awsAccessKeyId", credits.getCredentials.getAWSAccessKeyId)
    jobConf.set("dynamodb.awsSecretAccessKey", credits.getCredentials.getAWSSecretKey)
    jobConf.set("mapred.output.format.class", "org.apache.hadoop.dynamodb.write.DynamoDBOutputFormat")
    jobConf.set("mapred.input.format.class", "org.apache.hadoop.dynamodb.read.DynamoDBInputFormat")


    var orders = sc.hadoopRDD(jobConf, classOf[DynamoDBInputFormat], classOf[Text], classOf[DynamoDBItemWritable])

    //orders.map(t => t._2.getItem()).collect.foreach(println)
    val simple2: RDD[(String)] = orders.map { case (text, dbwritable) => (dbwritable.toString)}
    print(simple2.collect())
    /*val scanRequest = new ScanRequest()
      .withTableName(table);
    val result = DBCLient.scan(scanRequest)

    result.getItems.forEach(data =>
      data.values().forEach(d =>
        print
        (d.getS)
      )
    )

    val aaaah = result.getItems
    print(aaaah.getClass)
    */

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
