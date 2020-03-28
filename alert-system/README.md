# The PrestaCop Alert System
This service is responsible to filter all messages from the stream and handle the ones that require human interaction. In our case, those are all the messages that contain the violtion code **666**. If such a message is found, the message gets stored into a DynamoDB and an Alert is printed to the console.

## Running the Service using IntelliJ
To run the service, there are three configurable parameters to provide in the following order:
- **The AWS region**: Give a region in which your Kinesis stream and DynamoDB table can be found, e.g. "eu-central-1"
- **The Kinesis stream name**: This is the name of the Kinesis stream to publish to, e.g. "prestacop". Make sure it's a stream with only one shard
- **The DynamoDB table name**: This is the name of the DynamoDB table where all alert events are stored to, e.g. "prestacop"

Create a new run configuration for the **Consumer** class. Add the above options as program arguments.

## Running the jar (for production)
First build and package the application, then run the jar with the following command (provided example parameters):
```
java -jar <name>.jar "eu-central-1" "prestacop-kinesis-stream" "prestacop-dynamo-table"
```
