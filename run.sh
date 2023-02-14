#!/usr/bin/env bash
set -e
cd $(dirname $0)
cp build/libs/seefusion*.jar testpages/seefusion5.jar
cd testpages
if command -v podman &> /dev/null
then
	CONTAINER_MANAGER=podman
else
	CONTAINER_MANAGER=docker
fi
$CONTAINER_MANAGER build --tag seefusion-test .
podman run --rm -p 8888:8888 -p 8999:8999 seefusion-test
