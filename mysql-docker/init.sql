CREATE USER 'seefusion'@'%' IDENTIFIED BY 'seefusion';
CREATE DATABASE seefusion;
GRANT ALL PRIVILEGES ON seefusion.* TO `seefusion`@'%';
FLUSH PRIVILEGES
