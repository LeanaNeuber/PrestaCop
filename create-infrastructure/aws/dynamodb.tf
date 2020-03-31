resource "aws_dynamodb_table" "prestacop" {
  name = "prestacop_new"
  read_capacity = 5
  write_capacity = 5

  hash_key = "id"

  // Primary key
  attribute {
    name = "id"
    type = "S"
  }

  stream_enabled = true
  stream_view_type = "NEW_AND_OLD_IMAGES"
}