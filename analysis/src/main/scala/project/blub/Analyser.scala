package project.blub

import java.io.{File, PrintWriter}
import java.util

import com.amazonaws.services.dynamodbv2.model.AttributeValue
import org.apache.hadoop.dynamodb.DynamoDBItemWritable
import org.apache.hadoop.mapred.JobConf
import play.api.libs.json._
import project.Message
import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import org.apache.hadoop.dynamodb.read.DynamoDBInputFormat
import org.apache.hadoop.io.Text
import org.joda.time.format.DateTimeFormat

object Analyser {

  implicit val messageWrite: OFormat[Message] = Json.format[Message]

  def main(args: Array[String]): Unit = {

    if (args.length < 2)
      println("Two arguments are required to start this application: the aws region and the dynamodb table name!")

    val region = args(0)
    val table = args(1)

    val conf = new SparkConf()
      .setAppName("Analyser")
      .setMaster("local[*]") // here local mode. And * means you will use as much as you have cores.

    val sc = SparkContext.getOrCreate(conf)

    val jobConf = new JobConf(sc.hadoopConfiguration)
    jobConf.set("dynamodb.servicename", "dynamodb")
    jobConf.set("dynamodb.input.tableName", table)
    jobConf.set("dynamodb.regionid", region)
    jobConf.set("mapred.output.format.class", "org.apache.hadoop.dynamodb.write.DynamoDBOutputFormat")
    jobConf.set("mapred.input.format.class", "org.apache.hadoop.dynamodb.read.DynamoDBInputFormat")




    val messages = sc.hadoopRDD(jobConf, classOf[DynamoDBInputFormat], classOf[Text], classOf[DynamoDBItemWritable])
      .map(t => t._2.getItem)
      .map(item => toMessage(item))

    val countItems = messages.count()

    val countAlerts = messages
      .filter(m => m.violationCode.isDefined)
      .filter(m => m.violationCode.get.equals("666")) count()

    val topDrone = messages
      .map(m => m.droneId)
      .map((_, 1))
      .reduceByKey(_ + _).max()(Ordering[Int].on(x => x._2))

    val topMonth = messages
      .map(m => m.time)
      .map(m => getMonth(m))
      .filter(s => s.isDefined)
      .map(s => s.get)
      .map((_, 1))
      .reduceByKey(_ + _).sortBy(_._2).max()(Ordering[Int].on(x => x._2))

    val topViolationCode = messages
      .map(m => m.violationCode)
      .filter(m => m.isDefined)
      .map(c => c.get)
      .map((_, 1))
      .reduceByKey(_ + _).sortBy(_._2).max()(Ordering[Int].on(x => x._2))

    println("Count total :" + countItems)
    println("Count alerts: " + countAlerts)
    println("Drone of the month: " + topDrone._1 + " with number of detections: " + topDrone._2.toString())
    println("Top month: " + topMonth._1 + " with number of infractions: " + topMonth._2.toString())
    println("Top violation code: " + topViolationCode._1 + " with number of detections: " + topViolationCode._2.toString())
  }

  def getMonth(datestring: String): Option[String] = {
    try {
      val dtf = DateTimeFormat.forPattern("MM/dd/yyyy")
      Some(dtf.parseDateTime(datestring).getMonthOfYear.toString)
    } catch {
      case e: Exception => None
    }
  }


  def toMessage(item: util.Map[String, AttributeValue]): Message = {
    // We are ignoring the imageId, we don't use that anyways..
    val x = if (item.containsKey("violationCode"))
      new Message(item.get("location").getS, item.get("time").getS, item.get("droneId").getS, Option(item.get("violationCode").getS)) else
      new Message(item.get("location").getS, item.get("time").getS, item.get("droneId").getS)
    x
  }
}
