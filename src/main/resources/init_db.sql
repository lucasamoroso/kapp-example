-- Create database
CREATE SCHEMA IF NOT EXISTS kapp;

-- Create app user
-- DROP USER IF EXISTS 'local'@'%' ;
-- CREATE USER 'local'@'%' IDENTIFIED WITH mysql_native_password BY 'test';
-- GRANT ALL PRIVILEGES ON vectors.* TO 'local'@'%' WITH GRANT OPTION;

-- Create Tables
CREATE TABLE IF NOT EXISTS user (
 id int NOT NULL PRIMARY KEY AUTO_INCREMENT,
 name varchar(55) NOT NULL,
 last_name varchar(55) NOT NULL,
 age int NOT NULL
);

