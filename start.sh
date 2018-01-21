#!/usr/bin/env bash

set -e
set -o pipefail
set -u

finalize() {
    docker-compose stop
    docker-compose rm -v -f
}

trap finalize EXIT
trap exit ERR

./mvnw clean package jacoco:report
docker-compose up -d
sleep 10

xdg-open http://localhost:8080/ target/site/jacoco/index.html

docker-compose logs -f

exit 0