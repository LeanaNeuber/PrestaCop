package com.tp.spark.core

import org.apache.spark.{SparkConf, SparkContext, sql}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}
import org.apache.spark.sql.functions._



object Ex0WordcountDF {

  val pathToFile = "data/wordcount.txt"

  def loadData(): DataFrame = {
    // create spark configuration and spark context: the Spark context is the entry point in Spark.
    // It represents the connexion to Spark and it is the place where you can configure the common properties
    // like the app name, the master url, memories allocation...
    val conf = new SparkConf()
                        .setAppName("WordcountDF")
                        .setMaster("local[*]") // here local mode. And * means you will use as much as you have cores.

    val ss = SparkSession.builder()
      .appName("WordcountDF")
      .config(conf)
      .getOrCreate()

    ss.read.csv(pathToFile).toDF("line")
  }

  /**
   *  Now count how much each word appears!
   */
  def wordcount()  = {
    loadData()
      .withColumn("word", explode(split(col("line"), " ")))
      .withColumn("count", lit(1))
      .groupBy("word")
      .count
  }

  /**
   *  Now keep the word which appear strictly more than 4 times!
   */
  def filterOnWordcount() = {
    val df = wordcount()
    df.filter(df("count") > 4)
  }

  def main(args: Array[String]): Unit = {
    println("------------------------------------")
    filterOnWordcount().show
    println("------------------------------------")

  }
}
