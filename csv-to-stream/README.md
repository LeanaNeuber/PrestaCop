# The PrestaCop NYPD CSV to Kafka Service
This service is responsible for reading in the CSV files from the NYPD computers, translate every row into a message and push those messages into our stream for further processing.

## Running the Service using IntelliJ
To run the service, there are three configurable parameters to provide in the following order:
- **The AWS region**: Give a region in which your Kinesis stream and DynamoDB table can be found, e.g. "eu-central-1"
- **The Kinesis stream name**: This is the name of the Kinesis stream to publish to, e.g. "prestacop". Make sure it's a stream with only one shard
- **The path to the CSV file**: This is the path to the file that is supposed to be fed into the stream.

Create a new run configuration for the **Producer** class. Add the above options as program arguments.

## Running the jar (for production)
First build and package the application, then run the jar with the following command (provided example parameters):
```
java -jar <name>.jar "eu-central-1" "prestacop-kinesis-stream" "/data/NYPD_data.csv"
```
