output "dynamodb_table_name" {
  value = aws_dynamodb_table.prestacop.name
}

output "kinesis_stream_name" {
  value = aws_kinesis_stream.prestacop.name
}

output "region" {
  value = aws_instance.stream_to_storage.availability_zone
}

output "stream_to_storage_ipv4" {
  value = aws_instance.stream_to_storage.public_ip
}

output "alert_system_ipv4" {
  value = aws_instance.alert_system.public_ip
}

output "analysis_ipv4" {
  value = aws_instance.analysis.public_ip
}