--liquibase formatted sql
--changeset paula:003
CREATE TABLE courses
(
    id            BIGSERIAL PRIMARY KEY,
    title         VARCHAR(255) NOT NULL,
    professor_id  UUID,
    department_id BIGINT,
    FOREIGN KEY (professor_id) REFERENCES professors (id),
    FOREIGN KEY (department_id) REFERENCES departments (id)
);
--rollback