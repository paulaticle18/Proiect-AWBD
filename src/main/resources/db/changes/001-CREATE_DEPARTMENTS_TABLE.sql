--liquibase formatted sql
--changeset paula:001
CREATE TABLE departments
(
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);
--rollback