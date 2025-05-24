--liquibase formatted sql
--changeset paula:005
CREATE TABLE enrollment
(
    course_id  BIGINT NOT NULL,
    student_id UUID NOT NULL,
    PRIMARY KEY (course_id, student_id),
    FOREIGN KEY (course_id) REFERENCES courses (id),
    FOREIGN KEY (student_id) REFERENCES students (id)
);
--rollback