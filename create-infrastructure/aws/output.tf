output "dynamodb_table_name" {
  value = aws_dynamodb_table.prestacop.name
}

output "kinesis_stream_name" {
  value = aws_kinesis_stream.prestacop.name
}

output "region" {
  value = aws_instance.prestacop.availability_zone
}