resource "aws_kinesis_stream" "prestacop" {
  name             = "prestacop"
  shard_count      = 1
  retention_period = 48
}