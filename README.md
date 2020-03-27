<img align="left" width="80" height="80" src="lildrone.png" alt=">Drone icon">

# PrestaCop
This project is realized within the course **Functional Data Programming** at the **Efrei Paris**.

## Problem Description
**PrestaCop** is a company that wants to create a drone service to help the police make parking tickets.

Each drone sends messages regularly, with:
- drone location
- time
- drone id

And if a violation occured, with the two following additional fields:
- violation code
- image id

When the drone's message violation code indicates that human interaction is required (1% of the time), an alarm must be send to a human operator.

With all the data, PrestaCop wants to make statistics and improve their services. To improve those statistics, NYPD historical data should be used. However, NYPD poses two constraints:
- The computers are old and not very powerful
- The data is stored in a large CSV

## Architecture
The basic part of this project consists of the following 5 Services, a stream and a storage solution:
- **csv-to-stream**: Reads the NYPD CSV and publishes the rows as messages to the stream
- **drone-simulator**: Simulates a drone, sends messages of different forms to the stream
- **alert-system**: Consumes stream messages, raises an alarm when human interaction is required
- **stream-to-storage**: Consumes stream messages, stores them
- **analysis**: Reads messages out of storage, performs the analysis

The following picture depicts how the components work together:
![Architecture](Architecture_v1.png)

## Data Model
As described above, the drone sends messages wit 3 or 5 fields. We used a Scala case class **Message** to realize this data format:
```scala
case class Message(
                    location: String,
                    time: String,
                    droneId: String,
                    violationCode: Option[String] = None,
                    violationImageId: Option[String] = None
                  )
```

## Set up development environment
We are implementing this project in Scala, using a functional progamming approach.

### Use commandline tools
- Install JDK, eg. JDK-1.8:
- Install `sbt`, see [Download SBT](https://www.scala-sbt.org/download.html):

### Use IDE, eg. Intellij IDEA
- Install JDK, eg. JDK-1.8:
- [Download](https://www.jetbrains.com/idea/download/) and install Intellij IDEA
- Install _Scala_ and _SBT_ plugins
- Import project _as an SBT project_ (only available after installing the _SBT_ plugin)

## Set up AWS environment
- Open your AWS console
- Create a new IAM user that is able to write/read to/from Kinesis and S3
- To your home directory, add a file with the name ".aws.properties" with the following content (Add the credentials of the created user:
```
[default]
accessKey=addAccessKeyHere
secretKey=addSecretKeyHere
```
- Create a Kinesis stream with one shard, called "prestacop"
- Create a S3 Bucket with name and change the code to use this bucket.Env variables or command line arguments will be added later to specify it.
> **_Attention!:_**  Remember to always delete Kinesis after you finished your testing, as it will cost you.

## Set up Kafka environment
- Download Kafka (link)
- Start Kafka and Zookeeper
- Create "test" stream

## To Do...
- [ ] Implement the Spark Analysis (Colombe)
- [ ] Think about analysis that can be performed on the data (All)
- [ ] Add command line parameters or environment variables to all our services (Lea)
- [ ] Refactor all service, extract methods/classes and such (Céline, Florian)
- [ ] Implement an Alarm solution: Website? (Florian)
- [ ] Provide proper data cleansing on CSV

## Not yet
- [ ] Deployment of whole project to AWS: Terraform
- [ ] Provide CSV-to-Stream as fatjar

## Questions to ask
- [x] Can we do some services in another language than Scala? Example: the alert service (AWS Lambda can't be used in Scala)
  
  -> Everything can be done with Scala + Terraform. If we really have a problem regarding a service, we can send him an email.
  
  -> If not in Scala, in Java then (compile Scala into Java).
- [x] Ask about data cleaning 
  
  -> The program should not crash if the CSV isn't clean
  
  -> Not send the bad lines
- [x] Question: In what interval should our drone simulator send what messages? It says 1% of messages are alarms.

  -> We're not obligated to do an interval. A simulator sending 20 messages with 1 alarm is enough.

- [x] Question: Should the CSV-to-Stream be deployed to AWS?

  -> Nope, one-time thing are OK
