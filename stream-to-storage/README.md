# The PrestaCop Kafka Stream to Storage Service
This service is responsible for reading the messages from the Kinesis stream and storing them into DynamoDB.

## Running the Service using IntelliJ
To run the service, there are three configurable parameters to provide in the following order:
- **The AWS region**: Give a region in which your Kinesis stream and DynamoDB table can be found, e.g. "eu-central-1"
- **The Kinesis stream name**: This is the name of the Kinesis stream to publish to, e.g. "prestacop". Make sure it's a stream with only one shard
- **The DynamoDB table name**: This is the name of the table to store all the messages to.

Create a new run configuration for the **Consumer** class. Add the above options as program arguments.

## Running the jar (for production)
First build and package the application, then run the jar with the following command (provided example parameters):
```
java -jar <name>.jar "eu-central-1" "prestacop-kinesis-stream" "prestacop-dynamodb-table"
```

