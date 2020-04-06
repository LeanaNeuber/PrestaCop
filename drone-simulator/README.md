# The PrestaCop Drone Simulator
This service is responsible for simulating a drone that sends messages to our Kinesis stream. As of now, it sends 100 messages in a row, every 10th being one with the violation code ***666*** that triggers the alert service.

## Running the Service using IntelliJ
To run the service, there are three configurable parameters to provide in the following order:
- **The AWS region**: Give a region in which your Kinesis stream and DynamoDB table can be found, e.g. "eu-central-1"
- **The Kinesis stream name**: This is the name of the Kinesis stream to publish to, e.g. "prestacop". Make sure it's a stream with only one shard

Create a new run configuration for the **DroneSim** class. Add the above options as program arguments.

## Running the jar (for production)
First build and package the application, then run the jar with the following command (provided example parameters):
```
java -jar <name>.jar "eu-central-1" "prestacop-kinesis-stream"
```
