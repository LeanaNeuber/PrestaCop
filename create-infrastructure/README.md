# The PrestaCop NYPD Infrastructure Creator
This service is responsible for creating the AWS infrastructure that will gather the drone messages.

## Running the Service using command line interface
### Prerequisites
To run the service, you need the following:
#### AWS Credentials
You need to get AWS credentials on your computer. First, [download the AWS CLI here](https://docs.aws.amazon.com/cli/latest/userguide/install-cliv2-windows.html).

Then, do to [your IAM console](https://console.aws.amazon.com/iam/), and create a new user named Terraform. Give it the AdministratorAccess permissions.
At this point, go to the Security Credentials tab of your Terraform user. Press the button "Creates an access key", and keep both the access ID and access secret in a file.

Finally, run the following command: `aws configure`. If you wish to create a separated profile, run `aws configure --profile prestacop`.
Enter both the Access Key ID and the Access Key Secret that you got earlier.

Your AWS credentials are set up.

#### Get SSH key pair
You need a SSH key pair located in your ~/.ssh folder. They should be named "terraform" and "terraform.pub".

If you need to generate a ssh key, run the following command: `ssh-keygen -t rsa` and use `terraform` as a name.

If you already have a SSH key pair named differently, you can provide the name as an argument when creating the infrastructure (see below).

#### Get Terraform
Download the terraform client [here](https://www.terraform.io/downloads.html), and install it following your OS instructions.


### Creating the infrastructure
First, to in the "aws" directory: `cd aws`

Then, download the needed providers: `terraform init`
 
Finally, apply the infrastructure: `terraform apply -auto-approve`

If you need to provide a custom key name, add the following argument: `-var key_name=your_key_name`

If you want to use a specific AWS profile, add the following argument: `-var aws_profile=your_aws_profile`

### Destroy the infrastructure

Once done, you can destroy the infrastructure with the following command: `terraform destroy -auto-approve`