package project.blub

import java.io.File
import java.nio.charset.StandardCharsets

import play.api.libs.json._
import project.Message
import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.hadoop.dynamodb.read.DynamoDBInputFormat
import org.apache.hadoop.io.Text
import org.apache.spark


object Analyser {

  implicit val messageWrite = Json.format[Message]

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf()
      .setAppName("Analyser")
      .setMaster("local[*]") // here local mode. And * means you will use as much as you have cores.

    val sc = SparkContext.getOrCreate(conf)

    val data = sc.textFile("data/data.csv")

    //data.filter(isBad).foreach(println)

    val data2 = data
      .map(line => line.split(",}"))
      .map{ case Array(violationCode,droneId,violationImageId,location,time,id,ttl) => (getinfo(violationCode),getinfo(droneId),getinfo(violationImageId),getinfo(location),getinfo(time),getinfo(id),getinfo(ttl))}
    //data2.foreach(println)

    data2.filter(isBad2).foreach(println)


  }

  def isBad2(line : (String, String, String,String, String, String, String)) : Boolean ={
    line._1.equals("666")
  }

  def isBad(line : String) : Boolean = {
    line.contains("violationCode={S: 666,}")
  }

  def getinfo(truc : String) = {
    truc.split(": ")(1)
  }
}
