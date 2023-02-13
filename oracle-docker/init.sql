create user seefusion identified by Seefus1on QUOTA UNLIMITED ON users;
grant create session to seefusion;
grant create table to seefusion;
grant unlimited tablespace to seefusion;
ALTER USER seefusion QUOTA UNLIMITED ON SYSTEM;
