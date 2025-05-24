--liquibase formatted sql
--changeset paula:007
CREATE TABLE users
(
    id       UUID PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255)        NOT NULL,
    enabled  BOOLEAN
);
--rollback