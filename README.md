# Deliverypipeline [![Circle CI](https://circleci.com/gh/lachatak/deliverypipeline/tree/master.svg?style=svg)](https://circleci.com/gh/lachatak/deliverypipeline/tree/master)

## Continious Delivery on cloud ##
This is an experimental project to test how we could achieve continuous delivery pipeline with open source cloud based tools. 

### Tool set ###
- [AWS Elastic Beanstalk](http://aws.amazon.com/elasticbeanstalk/) to host our application
- [Docker](https://www.docker.com/) to make the application more portable
- [Dockerhub](https://hub.docker.com/) to store Docker images produced by the CI
- [Mongolab](https://mongolab.com/) to have a cloud based mongo store for the test application to store event source snapshots
- [CircleCI](https://circleci.com/) to build and deploy the application
- [Github](https://github.com/lachatak) to store the application source
- The application itself is a [Spray](http://spray.io/) and [Akka](http://akka.io/) based simple REST application

The running application is available [here](http://deliverypipeline-prod.elasticbeanstalk.com/)

### The main concept ###

## The application ##
I have a simple REST based application. It provides some basic information about its running environment:
- Deploy history
- Aggregated number of calls since the first version was deployed
- Currently deployed version
- Host name
The application has Akka mongo persistence. Every time the application URL is called the internal state will be modified and persisted to a mongo store which is hosted by Mongolab. If I deploy a new version of the application it is going to use the same mongo store and fetch the previously persisted state.

## Continous Integration ##
When ever I push a modification to the github repository my cloud based CircleCI is going to pick up the modification and build the new version of the application.
The build has the following steps:
- Prepare the build environment
- Deploy required dependencies like awscli
- Run application unit tests
- Build docker image from the application and run it on CircleCI
- Run integration tests agains the previously constructed docker image to very that the image is functional. Now it is just a simple curl request but it could be a more complex integration test or even load test.
- If all the test pass the image is uploaded to Dockerhub
- Update Dockerrun.aws.json to point to the newly created application version in Dockerhub
- Create a new application version in AWS using the new Dockerrun.aws.json
- Pick the preproduction environment for deployment based on the public URL
- Deploy the new application version to the preproduction environment
- Wait as long as the environment is ready again after the deployment
- Swap the preproduction and production URL. The preproduction environment will become the new production and vica versa. For the next deployment the new preproduction system will be used 

