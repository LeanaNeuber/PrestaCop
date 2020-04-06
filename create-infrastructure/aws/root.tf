variable "aws_profile" {
  default = "prestacop"
}

variable "key_name" {
  description = "Your SSH private key name. Has to be located in your ~/.ssh/ folder"
  default     = "terraform"
}

variable "aws_region" {
  description = "The AWS region"
  default     = "eu-central-1"
}

provider "aws" {
  profile = var.aws_profile
  region  = var.aws_region
}
