#!/usr/bin/env bash
# Creates a microsoft sql server daemon for testing
docker network exists seefusion-test || docker create seefusion-test
docker build mssql-docker -t seemssql
docker stop mssql || true
docker run --name mssql --rm -d --expose 1433 -p 1433:1433 --net seefusion-test -e 'ACCEPT_EULA=Y' -e 'SA_PASSWORD=StrongPassw0rd' seemssql