#!/bin/sh
sbt exportVersionNumber --warn
chmod 774 setup.sh
. ./setup.sh
sed  -i "s/APP_VERSION/$APP_VERSION/" Dockerrun.aws.json
release/release.rb