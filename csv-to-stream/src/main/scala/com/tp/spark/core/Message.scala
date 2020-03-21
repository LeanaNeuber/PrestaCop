package com.tp.spark.core

case class Message(
                    location: String,
                    time: String,
                    droneId: String,
                    violationCode: Option[String] = None,
                    violationImageId: Option[String] = None
                  )