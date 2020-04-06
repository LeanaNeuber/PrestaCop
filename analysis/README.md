# The PrestaCop Analyser
This service is responsible for retrieving the data stored in DynamoDB and analysing this data using spark. The results of the analysis are stored in a file called **"result.txt"** 

## Running the Service using IntelliJ
To run the service, there are two configurable parameters to provide in the following order:
- **The AWS region**: Give a region in which your DynamoDB table can be found, e.g. "eu-central-1"
- **The DynamoDB table name**: This is the name of the DynamoDB table in which all drone messages are stored

Create a new run configuration for the **Analyser** class. Add the above options as program arguments.

## Running the jar (for production)
First build and package the application, then run the jar with the following command (provided example parameters):
```
java -jar <name>.jar "eu-central-1" "prestacop-dynamodb-table"
```
