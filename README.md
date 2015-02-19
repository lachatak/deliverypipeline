# Deliverypipeline [![Circle CI](https://circleci.com/gh/lachatak/deliverypipeline/tree/master.svg?style=svg)](https://circleci.com/gh/lachatak/deliverypipeline/tree/master)

## Continious Delivery on cloud ##
This is an experimental project to test how we could achieve continuous delivery pipeline with open source cloud based tools. 

### Tool set ###
- [AWS Elastic Beanstalk](http://aws.amazon.com/elasticbeanstalk/) to host our application
- [Docker](https://www.docker.com/) to make the application more portable
- [Dockerhub](https://hub.docker.com/) to store Docker images produced by the CI
- [Mongolab](https://mongolab.com/) to have a cloud based mongo store for the test application
- [CircleCI](https://circleci.com/) to build and deploy the application
- [Github](https://github.com/lachatak) to store the application source
- The application itself is a Spray and Akka based simple REST application

The running application is available [here](http://deliverypipeline-prod.elasticbeanstalk.com/)
