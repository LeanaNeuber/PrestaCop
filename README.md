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
- 5 Services as basic part: describe shortly
- Extra touch
- Deployment to cloud.. or not 

## Data Model
As described above, the drone sends messages wit 3 or 5 fields. We used a 

## Development Process
- git.. explain 
- One branch: master --> Needs super good communication!
- Think about the Kinesis stream: Create and delete again!

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
- Create a new IAM role that is able to write/read to/from Kinesis and S3
- Create a Kinesis stream with one shard, called "prestacop"
- Create a S3 Bucket with name close to "prestacop" (I already used this, so you won't be able to. Just remember to change the code to read from your bucket for now. Env variables or command line arguments will be added later)
- !!!! Remember to always delete Kinesis after you finished your testing, as it will cost you.

## Set up Kafka environment
- Download Kafka (link)
- Start Kafka and Zookeeper
- Create "test" stream

## To Do...
- [ ] Provide proper data cleansing on CSV
- [ ] Implement the Spark Analysis
- [ ] Think about analysis that can be performed on the data
- [ ] Add command line parameters or environment variables to all our services
- [ ] Refactor all service, extract methods/classes and such
- [ ] Implement cosumer to HDFS **if** we don't use S3
- [ ] Implement an Alarm solution: Website? 
- [ ] Deployment of whole project to AWS: Terraform
- [ ] Provide CSV-to-Stream as fatjar
- [ ] Question: In what interval should our drone simulator send what messages? It says 1% of messages are alarms.
- [ ] Question: Usage of Docker for deployment?
- [ ] Question: Should the CSV-to-Stream be deployed to AWS?
- [ ] Question: More "own touch"?
