# Deliverypipeline [![Circle CI](https://circleci.com/gh/lachatak/deliverypipeline/tree/master.svg?style=svg)](https://circleci.com/gh/lachatak/deliverypipeline/tree/master) [![Coverage Status](https://coveralls.io/repos/lachatak/deliverypipeline/badge.svg?branch=master)](https://coveralls.io/r/lachatak/deliverypipeline?branch=master)

## Continuous Delivery on cloud ##
This is an experimental project to test how we could achieve continuous delivery pipeline with open source cloud based tools to enable zero downtime release.
The main motivation was this documentation from AWS:
http://docs.aws.amazon.com/elasticbeanstalk/latest/dg/using-features.CNAMESwap.html

### Tool set ###
- [AWS Elastic Beanstalk](http://aws.amazon.com/elasticbeanstalk/) to host our application
- [Docker](https://www.docker.com/) to make the application more portable
- [Dockerhub](https://hub.docker.com/) to store Docker images produced by the CI
- [Mongolab](https://mongolab.com/) to have a cloud based mongo store for the test application to store event source snapshots
- [CircleCI](https://circleci.com/) to build and deploy the application
- [Github](https://github.com/lachatak) to store the application source
- The application itself is a [Spray](http://spray.io/) and [Akka](http://akka.io/) based simple REST application

The running application is available [here](http://deliverypipeline-prod.elasticbeanstalk.com/)

## The ingredients ##

### The application ###
I have a simple REST based application. It provides some basic information about its running environment:
- Deploy history
- Aggregated number of calls since the first version was deployed
- Currently deployed version
- Host name

The application has Akka mongo persistence. Every time the application URL is called the internal state will be modified and persisted to a mongo store which is hosted by Mongolab. If I deploy a new version of the application it is going to use the same mongo store and fetch the previously persisted state.

The application requires a ***DELIVERY_CONF*** env property which should point to a configuration file. If the env property is missing it is going to use the default LevelDB local persistence.

### sbt plugins ###
To achieve the well desired goal I had to add some sbt plugins:
- [sbt-docker](https://github.com/marcuslonnberg/sbt-docker) to manage Docker image generation
- [sbt-buildinfo](https://github.com/sbt/sbt-buildinfo) to generate a class that contains static build time information like git version number, generation time. It is used by the server when the user hits a endpoint
- [sbt-git](https://github.com/sbt/sbt-git) to modify the application version number to contain git version number

### AWS Elastic Beanstalk ###
- I created one elastic beanstalk application on AWS with the name ***deliverypipeline***
- It has two environments ***deliverypipeline-node-1*** and ***deliverypipeline-node-2***. The first has ***deliverypipeline-prod.elasticbeanstalk.com*** public URL meanwhile the other has ***deliverypipeline-preprod.elasticbeanstalk.com***. Both of the environments will host the Dockerized version of the aforementioned Spray REST application. They have a previously configured ***DELIVERY_CONF*** which point to a local file */app/application.conf*. This file is used as a **volume** for the generated and used Docker image. That is the way how the live application has proper configuration relevant to the environment. 

### Continous Integration ###
When ever I push a modification to the github repository my cloud based CircleCI is going to pick up the modification and build the new version of the application.
The build has the following steps:
- Prepare the build environment
- Deploy required dependencies like awscli
- Run application unit tests
- Build docker image from the application and run it on CircleCI build box
- Run integration tests agains the previously constructed and running docker image to very that the image is functional, all the ports wihch is needed are exposed. The test currently is just a simple curl request but it could be a more complex integration test or even load test for complex builds. This environment doesn't have ***DELIVERY_CONF*** env property so the application is using the configuration provided inside the docker image which is by default points to a local LevelDB for storing event stanpshots.
- If all the test pass the image will be uploaded to Dockerhub
- Update Dockerrun.aws.json to point to the newly created docker image version in Dockerhub
- Create a new application version in AWS using the new, modified Dockerrun.aws.json
- Pick the preproduction environment for deployment based on the public URL
- Deploy the new application version to the preproduction environment
- After the deployment wait as long as the environment is ready again 
- Swap the preproduction and production URL. The preproduction environment will become the new production and vica versa. For the next deployment the new preproduction system will be used 

All the steps described here can be followed in the [CircleCi configuration file](circle.yml) added to the projects root directory. 

## The process ##
- Push modifications to the github
- CircleCI discovers that there is a new version to deploy
- CircleCI goes through the build process and generates a new docker image
- The new image will be uploaded to Dockerhub
- CircleCI creates a new application version in AWS and deploys this version to the preprod environment which has ***deliverypipeline-preprod.elasticbeanstalk.com*** URL. AWS will pull the new version from the Dockerhub and install it the the preproduction environment
- CircleCI verifies the deployment and swaps the preproduction and production URL if the verification was successful. However, DNS propagation requires some time to happen. DNS servers do not necessarily clear old records from their cache based on the time to live (TTL). Based on this fact you could possible wait until the swap has a real effect.
- The user goes to the production URL and hits the page. The application updates its internal state and persists the state to the Mongolab mongo database. This configuration is coming from the */app/application.conf* live configuration which is mapped into the Docker image via the Dockerrun.aws.json
- After the new deployment the freshly started application picks up the persisted state and continues the operation

Obviously in a real application it could be even more complex but it is a good basic solution for further development.

## Zero downtime release delivered!!! ##

 

