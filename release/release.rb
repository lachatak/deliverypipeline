#! /usr/bin/env ruby
require 'optparse'
require 'json'
require 'timeout'

application_name = "deliverypipeline"
environment1 = "deliverypipeline-dev"
environment2 = "deliverypipeline-dev2"

backup_url = "deliverypipeline-dev2.elasticbeanstalk.com"

puts "Starting zero downtime deployment for application: #{application_name}"

results = JSON.parse(%x[aws elasticbeanstalk describe-environments --application-name #{application_name} --region eu-west-1])

target_environment_name = results['Environments'].select { |item| item['CNAME']==backup_url }.collect {|item| item['EnvironmentName'] }[0]

puts "Deploying version to environment #{target_environment_name}"

%x[eb init deliverypipeline -r eu-west-1  -p docker]
%x[eb use #{target_environment_name}]
%x[eb deploy #{target_environment_name}]

results = JSON.parse(%x[aws elasticbeanstalk describe-environments --application-name #{application_name} --environment-names #{target_environment_name} --region eu-west-1])['Environments'][0]

env_health = results['Health']
env_status = results['Status']
puts "Current Health: #{env_health}   Current Status: #{env_status}"
if (("Green" != env_health) || ("Ready" != env_status))
  abort "Environment must be Ready and Green. Current Health: #{env_health}  Current Status: #{env_status}"
end

puts "Deployment was successful to #{application_name} -> #{target_environment_name}!"

puts "Swap URLs for #{environment1} -> #{environment2}!"

%x[aws elasticbeanstalk swap-environment-cnames --source-environment-name #{environment1} --destination-environment-name #{environment2} --region eu-west-1]

puts "Deployment finished!"

