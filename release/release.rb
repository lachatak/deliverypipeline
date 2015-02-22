#! /usr/bin/env ruby
require 'optparse'
require 'json'
require 'timeout'

application_name = "deliverypipeline"
environment1 = "deliverypipeline-node-1"
environment2 = "deliverypipeline-node-2"
preprod_url = "deliverypipeline-preprod.elasticbeanstalk.com"
s3bucket = "deliverypipeline"
region = "eu-west-1"
app_version = ENV['APP_VERSION']

puts "Starting zero downtime deployment for application: #{application_name}"

results = JSON.parse(%x[aws elasticbeanstalk describe-environments --application-name #{application_name} --region #{region}])

target_environment_name = results['Environments'].select { |item| item['CNAME']==preprod_url }.collect {|item| item['EnvironmentName'] }[0]

puts "Deploying version to environment #{target_environment_name}"

%x[aws s3api put-object --bucket #{s3bucket} --key #{app_version}.json  --body Dockerrun.aws.json --region #{region}]
%x[aws elasticbeanstalk create-application-version --application-name deliverypipeline --version-label #{app_version} --source-bundle S3Bucket=#{s3bucket},S3Key=#{app_version}.json --region #{region}]
%x[aws elasticbeanstalk update-environment --environment-name #{target_environment_name} --version-label #{app_version} --region #{region}]

## Wait for the new environment to go ready
## This wait can be customized for a given application to determine when it is really ready

max_wait_time_sec = 10 * 60;  ## 10 minutes
begin
  Timeout::timeout(max_wait_time_sec) do
    done = false
    while (! done) do
      puts "Checking if new environment is ready/green"
      results = JSON.parse(%x[aws elasticbeanstalk describe-environments --application-name #{application_name} --environment-names #{target_environment_name} --region #{region}])['Environments'][0]

      env_health = results['Health']
      env_status = results['Status']
      puts "Current Health: #{env_health} Current Status: #{env_status}"
      done = "Ready" == env_status
      if (! done)
        sleep 20  ## seconds
      end
    end
  end
rescue Timeout::Error
  abort "Environment does not seem to be launching in a reasonable time.  Exiting after #{max_wait_time_sec} seconds"
end

puts "Deployment was successful to #{application_name} -> #{target_environment_name}!"

puts "Swap URLs for #{environment1} -> #{environment2}!"

%x[aws elasticbeanstalk swap-environment-cnames --source-environment-name #{environment1} --destination-environment-name #{environment2} --region #{region}]

puts "Deployment finished!"

