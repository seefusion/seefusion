#!/usr/bin/env bash
# Creates a microsoft sql server daemon for testing
docker build mssql-docker -t seemssql
docker run --name=mssql --rm -d --expose 1433 -p 1433:1433 --network-alias mssql -e 'ACCEPT_EULA=Y' -e 'SA_PASSWORD=StrongPassw0rd' seemssql