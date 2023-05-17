terraform {
  required_providers {
        aws = {
          source = "hashicorp/aws"
          version = "~> 4.0"
        }
   }
}


provider "aws" {
   region = "us-east-1"
}

resource "aws_instance" "jenkin" {
  ami                     = "ami-007855ac798b5175e"
  instance_type           = "t2.micro"
  key_name                = "my_awskey"

tags = {
  name = "jenkins"
}

user_data = <<-EOF
 #!/bin/bash
 apt update -y
 curl -fsSL https://pkg.jenkins.io/debian-stable/jenkins.io-2023.key | sudo tee \
    /usr/share/keyrings/jenkins-keyring.asc > /dev/null
 echo deb [signed-by=/usr/share/keyrings/jenkins-keyring.asc] \
    https://pkg.jenkins.io/debian-stable binary/ | sudo tee \
    /etc/apt/sources.list.d/jenkins.list > /dev/null
 sudo apt-get update
  sudo apt-get install fontconfig openjdk-11-jre
  sudo apt-get install jenkins
  systemctl enable jenkins
  systemctl start jenkins
  EOF
}
resource "aws_security_group" "jenkin_sec_grp" {
  name        = "jenkin_sec_grp"
  description = "Allow SSH and HTTP traffic"
  vpc_id      = "vpc-0b9fe05aab8762565"

  ingress {
    description      = "SSH from VPC"
    from_port        = 443
    to_port          = 443
    protocol         = "tcp"
    cidr_blocks      = ["0.0.0.0/0"]

  }

  ingress {
    description      = "tcp from VPC"
    from_port        = 22
    to_port          = 22
    protocol         = "tcp"
    cidr_blocks      = ["0.0.0.0/0"]

}

  ingress {
    description      = "port from VPC"
    from_port        = 8080
    to_port          = 8080
    protocol         = "tcp"
    cidr_blocks      = ["0.0.0.0/0"]

}

  egress {
    from_port        = 0
    to_port          = 0
    protocol         = "-1"
    cidr_blocks      = ["0.0.0.0/0"]

  }

  tags = {
    Name = "Jenkins_sec_grp"
  }
}
  
