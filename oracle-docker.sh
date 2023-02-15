#!/usr/bin/env bash
# Creates an oracle daemon for testing
docker build oracle-docker -t seeoracle
docker stop oracle || true
docker run --name oracle --net seefusion-test --rm -d --expose 1521 --publish 1521:1521 -e ORACLE_ALLOW_REMOTE=true --shm-size="1g" --hostname oracle seeoracle
