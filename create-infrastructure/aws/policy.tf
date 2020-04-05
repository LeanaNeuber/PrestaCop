resource "aws_iam_instance_profile" "stream_to_storage_profile" {
  name = "stream_to_storage_profile"
  role = aws_iam_role.role.name

  depends_on = [aws_iam_role_policy.dynamodb_full, aws_iam_role_policy.kinesis_read]
}

resource "aws_iam_role" "role" {
  name = "stream_to_storage_role"
  path = "/"

  assume_role_policy = <<EOF
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Action": "sts:AssumeRole",
            "Principal": {
               "Service": "ec2.amazonaws.com"
            },
            "Effect": "Allow",
            "Sid": ""
        }
    ]
}
EOF
}

resource "aws_iam_role_policy" "kinesis_read" {
  name = "kinesis_read"
  role = aws_iam_role.role.id

  policy = <<-EOF
  {
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "kinesis:Get*",
                "kinesis:List*",
                "kinesis:Describe*"
            ],
            "Resource": "*"
        }
    ]
  }
  EOF
}

resource "aws_iam_role_policy" "dynamodb_full" {
  name = "dynamodb_full"
  role = aws_iam_role.role.id

  policy = <<-EOF
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Action": [
                "dynamodb:*"
            ],
            "Effect": "Allow",
            "Resource": "*"
        }
    ]
  }
  EOF
}
