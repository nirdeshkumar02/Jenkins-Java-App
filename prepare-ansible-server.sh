#!/usr/bin/env bash

apt update
apt install ansible -y
apt install python3-pip -y
pip3 install boto3 botocore