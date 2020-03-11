package com.tp.spark.core

import org.apache.spark.SparkConf
import org.apache.spark.sql.{DataFrame, SparkSession}

object ReadCsv {

  val pathToFile = "data/nyc-parking-tickets/Parking_Violations_Issued_-_Fiscal_Year_2014__August_2013___June_2014_.csv"

  def loadData(): DataFrame = {
    // create spark configuration and spark context: the Spark context is the entry point in Spark.
    // It represents the connexion to Spark and it is the place where you can configure the common properties
    // like the app name, the master url, memories allocation...
    val conf = new SparkConf()
      .setAppName("WordcountDF")
      .setMaster("local[*]") // here local mode. And * means you will use as much as you have cores.

    val ss = SparkSession.builder()
      .appName("CSV2Stream")
      .config(conf)
      .getOrCreate()

    //ss.read.csv(pathToFile).toDF()
    ss.read
      .format("csv")
      .option("header", "true")
      .option("inferSchema", "true")
      .load(pathToFile)
      .select("Issue Date", "Violation Code", "Violation Location", "Violation Time", "Violation Legal Code")

  }


  //iterator --> read each line --> drop header --> map to schema (case 48 columns do smth, case 51 do something, case else drop the line ) --> case class --> send to producer

  /**
    *  Now count how much each word appears!
    */
  def readCsv()  = {
    loadData().show(5)

      /*.withColumn("word", explode(split(col("line"), " ")))
      .withColumn("count", lit(1))
      .groupBy("word")
      .count*/
  }


  def main(args: Array[String]): Unit = {
    println("------------------------------------")
    readCsv()
    println("------------------------------------")

  }

}
