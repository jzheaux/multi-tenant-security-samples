CREATE SCHEMA IF NOT EXISTS widgets;
CREATE SCHEMA IF NOT EXISTS toasters;
CREATE SCHEMA IF NOT EXISTS socks;

CREATE TABLE widgets.message (id BIGINT PRIMARY KEY, to VARCHAR2(256) NOT NULL, from_address VARCHAR2(256) NOT NULL, text TEXT NOT NULL);
CREATE TABLE toasters.message (id BIGINT PRIMARY KEY, to VARCHAR2(256) NOT NULL, from_address VARCHAR2(256) NOT NULL, text TEXT NOT NULL);
CREATE TABLE socks.message (id BIGINT PRIMARY KEY, to VARCHAR2(256) NOT NULL, from_address VARCHAR2(256) NOT NULL, text TEXT NOT NULL);