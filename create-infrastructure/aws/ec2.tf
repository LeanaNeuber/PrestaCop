resource "aws_key_pair" "generated_key" {
  key_name   = var.key_name
  public_key = file("~/.ssh/${var.key_name}.pub")
}

// Allows SSH & HTTP to our instance
resource "aws_security_group" "allow_ssh" {
  name        = "allow_ssh_http"
  description = "Allow SSH inbound traffic, and HTTP/HTTPS outbound traffic"

  ingress {
    description = "Allow SSH"
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"] // Source can be anywhere
  }

  egress {
    from_port = 80
    to_port = 80
    protocol = -1
  }

  egress {
    from_port = 443
    to_port = 443
    protocol = -1
  }

  tags = {
    Name = "allow_ssh"
  }
}

// Our EC2 instance
resource "aws_instance" "prestacop" {
  ami           = "ami-0ec1ba09723e5bfac"
  instance_type = "t2.micro"
  key_name      = aws_key_pair.generated_key.key_name
  security_groups = [aws_security_group.allow_ssh.name]

  // Used to connect to our EC2 instance
  connection {
    type        = "ssh"
    user        = "ec2-user"
    timeout     = "1m"
    private_key = file("~/.ssh/${var.key_name}")
    host        = self.public_ip
  }

  // Do some things on instance start
  provisioner "remote-exec" {
    inline = [
      "echo Successfully connected via SSH to the instance!",
    ]
  }
}
