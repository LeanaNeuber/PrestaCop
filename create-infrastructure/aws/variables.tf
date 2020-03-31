variable "aws_profile" {
  default = "default"
}

variable "key_name" {
  default = "terraform"
}

provider "aws" {
  profile = var.aws_profile
  region = "eu-central-1"
}