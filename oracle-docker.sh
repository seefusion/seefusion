#!/usr/bin/env bash
# Creates an oracle daemon for testing
docker build oracle-docker -t seeoracle
docker stop seeoracle
docker run --name=oracle --rm -d --expose 1521 --publish 1521:1521 -e ORACLE_ALLOW_REMOTE=true --shm-size="1g" --hostname oracle seeoracle
