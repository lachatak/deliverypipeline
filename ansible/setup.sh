#!/bin/sh
export ANSIBLE_HOSTS=/etc/ansible/ec2.py
export EC2_INI_PATH=/etc/ansible/ec2.ini

ansible-playbook ebs.yml -i /etc/ansible/hosts
ansible-playbook appconfig.yml --ask-vault-pass