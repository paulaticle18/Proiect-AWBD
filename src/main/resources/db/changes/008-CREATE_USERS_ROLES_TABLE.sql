--liquibase formatted sql
--changeset paula:008
CREATE TABLE users_roles
(
    user_id UUID   NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (role_id) REFERENCES roles (id)
);
--rollback