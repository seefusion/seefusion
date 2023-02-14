create database seefusion
go
create login seefusion with password='Seefus1on', default_database=seefusion
go
use seefusion
go
create user seefusion for login seefusion
go
exec sp_addrolemember 'db_owner', 'seefusion'
go