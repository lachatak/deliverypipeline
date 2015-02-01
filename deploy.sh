#! /bin/bash

SHA1=$1
EB_BUCKET=elasticbeanstalk-eu-west-1-715628085737

echo $EB_BUCKET

# Create new Elastic Beanstalk version
DOCKERRUN_FILE=$SHA1-Dockerrun.aws.json
sed "s/<TAG>/$SHA1/" < Dockerrun.aws.json.template > $DOCKERRUN_FILE

echo $DOCKERRUN_FILE

aws s3 cp $DOCKERRUN_FILE s3://$EB_BUCKET/$DOCKERRUN_FILE

echo 1

aws elasticbeanstalk create-application-version --application-name hello \
    --version-label $SHA1 --source-bundle S3Bucket=$EB_BUCKET,S3Key=$DOCKERRUN_FILE

echo 2

# Update Elastic Beanstalk environment to new version
aws elasticbeanstalk update-environment --environment-name hello-env --version-label $SHA1

echo 3