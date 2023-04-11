#!/usr/bin/env bash
set -e -o pipefail
./gradlew clean build
docker build --no-cache -t spark-job .

