package com.project.spark.core

import java.io.File
import java.nio.ByteBuffer
import com.amazonaws.auth.{AWSCredentials, AWSCredentialsProvider, PropertiesCredentials}
import com.amazonaws.services.kinesis.model.PutRecordRequest
import com.amazonaws.services.kinesis.{AmazonKinesis, AmazonKinesisClientBuilder}
import play.api.libs.json.{Json, OWrites}

import scala.io.Source

object Producer {

  implicit val residentWrites: OWrites[Message] = Json.writes[Message]

  def main(args: Array[String]): Unit = {
    if (args.length < 3)
      println("Three arguments are required to start this application: the aws region, the stream, and the path to the csv file!")

    val region = args(0)
    val stream = args(1)
    val pathToFile = args(2)

    val kinesisClient = buildKinesisClient(region, getCreds)

    extractCSV(stream, pathToFile, kinesisClient)
  }

  private def extractCSV(stream: String, pathToFile: String, kinesisClient: AmazonKinesis) = {
    val bufferedSource = Source.fromFile(pathToFile)

    val header = bufferedSource.getLines.next().split(",").toList

    bufferedSource
      .getLines()
      .drop(1)
      .foreach(line => extractLines(stream: String, kinesisClient, header, line.split(",").toList))

    bufferedSource.close()
  }

  //map to schema (case 48 columns do smth, case 51 do something, case else drop the line ) --> case class wtf?
  private def extractLines(stream: String, kinesisClient: AmazonKinesis, header: List[String], lineList: List[String]) = lineList match {
      case x if lineList.size > header.indexOf("Street Name") => {
        val location = x(header.indexOf("House Number")) + " " + x(header.indexOf("Street Name"))
        val time = x(header.indexOf("Issue Date"))
        val violationCode = x(header.indexOf("Violation Code"))
        writeToKinesis(stream, kinesisClient, "test", Message(location, time, scala.util.Random.nextInt(100000).toString, Some(violationCode)))
      }
      case _ =>
  }

  def writeToKinesis(stream: String, kinesisClient: AmazonKinesis, topic: String, message: Message): Unit = {
    val putRecordRequest = new PutRecordRequest
    putRecordRequest.setStreamName(stream)
    putRecordRequest.setPartitionKey("key")
    putRecordRequest.setData(ByteBuffer.wrap(Json.toJson(message).toString().getBytes()))

    kinesisClient.putRecord(putRecordRequest)
  }

  private def buildKinesisClient(region: String, credentialsProvider: AWSCredentialsProvider) = {
    val clientBuilder = AmazonKinesisClientBuilder.standard()
    clientBuilder.setRegion(region)
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
