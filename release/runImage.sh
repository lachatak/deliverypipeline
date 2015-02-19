#!/bin/sh
sbt exportVersionNumber --warn
chmod 774 setup.sh
. ./setup.sh
docker run -d -p 8080:8080 lachatak/deliverypipeline:$APP_VERSION
sleep 10