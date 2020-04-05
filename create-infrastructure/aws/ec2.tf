resource "aws_key_pair" "ssh_key" {
  key_name   = var.key_name
  public_key = file("~/.ssh/${var.key_name}.pub")
}

// Allows SSH & HTTP to our instance
resource "aws_security_group" "allow_ssh" {
  name        = "allow_ssh_http"
  description = "Allow SSH inbound traffic, and HTTP/HTTPS outbound traffic"

  ingress {
    description = "Allow inbound icmp"
    protocol = "icmp"
    from_port = 0
    to_port = 0
  }

  egress {
    description = "Allow outbound icmp"
    protocol = "icmp"
    from_port = 0
    to_port = 0
  }

  ingress {
    description = "Allow SSH"
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"] // Source can be anywhere
  }

  egress {
    description = "Allow to use HTTP"
    from_port = 80
    to_port = 80
    protocol = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    description = "Allow to use HTTPS"
    from_port = 443
    to_port = 443
    protocol = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    description = "Allow to use HTTPS"
    from_port = 443
    to_port = 443
    protocol = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    description = "Allow to use DNS"
    from_port = 53
    to_port = 53
    protocol = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "allow_connection"
  }
}

// Our EC2 instance
resource "aws_instance" "stream_to_storage" {
  ami           = "ami-0ec1ba09723e5bfac"
  instance_type = "t2.micro"
  key_name      = aws_key_pair.ssh_key.key_name
  security_groups = [aws_security_group.allow_ssh.name]

  iam_instance_profile = aws_iam_instance_profile.stream_to_storage_profile.name

  depends_on = [aws_kinesis_stream.prestacop, aws_dynamodb_table.prestacop]

  // Execute commands on the instance once it is setup
  user_data = file("stream_to_storage.yml")


  // Used to connect to our EC2 instance
  connection {
    type        = "ssh"
    user        = "ec2-user"
    timeout     = "2m"
    private_key = file("~/.ssh/${var.key_name}")
    host        = self.public_ip
  }

  provisioner "file" {
    source = "../../stream-to-storage/target/scala-2.13/Stream to Storage System-assembly-1.0.jar"
    destination = "/home/ec2-user/stream_to_storage.jar"
  }
}
