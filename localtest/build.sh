#!/usr/bin/env bash
set -ex
cp ../../seefusion/build/libs/seefusion*.jar seefusion5.jar
if command -v podman &> /dev/null
then
	CONTAINER_MANAGER=podman
else
	CONTAINER_MANAGER=docker
fi
$CONTAINER_MANAGER build --tag dbanttari/seefusion-lucee-docker-5 .
