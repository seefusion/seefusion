#!/usr/bin/env bash
# Creates a mssql daemon for testing
docker pull microsoft/mssql-server-linux
docker run --rm --name sqlserver -e "ACCEPT_EULA=Y" -e "SA_PASSWORD=Seefus1on"  -e "MSSQL_PID=Express" -p 1433:1433 -d microsoft/mssql-server-linux
docker exec sqlserver /opt/mssql-tools/bin/sqlcmd -U sa -P Seefus1on -q "create database seefusion"
docker exec sqlserver /opt/mssql-tools/bin/sqlcmd -U sa -P Seefus1on -q "create login seefusion with password='Seefus1on', default_database=seefusion"
docker exec sqlserver /opt/mssql-tools/bin/sqlcmd -U sa -P Seefus1on -d seefusion -q "create user seefusion for login seefusion"
docker exec sqlserver /opt/mssql-tools/bin/sqlcmd -U sa -P Seefus1on -d seefusion -q "exec sp_addrolemember 'db_owner', 'seefusion'"
