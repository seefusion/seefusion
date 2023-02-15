#!/usr/bin/env bash
# Creates a mysql daemon for testing
docker network exists seefusion-test || docker create seefusion-test
docker build mysql-docker -t seemysql
docker stop mysql || true
docker run --name mysql --rm -d --expose 3306 --publish 3306:3306 --net seefusion-test -e MYSQL_ROOT_PASSWORD=seefusion seemysql
