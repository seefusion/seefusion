#!/usr/bin/env bash
# Creates a mysql daemon for testing
docker build mysql-docker -t seemysql
docker run --name=mysql --rm -d --expose 3306 --publish 3306:3306 --network-alias mysql -e MYSQL_ROOT_PASSWORD=seefusion seemysql
