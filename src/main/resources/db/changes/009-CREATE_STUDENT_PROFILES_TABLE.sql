CREATE TABLE IF NOT EXISTS student_profiles (
    id UUID PRIMARY KEY,
    address VARCHAR(255) NOT NULL,
    phone_number VARCHAR(255) NOT NULL,
    student_id UUID UNIQUE NOT NULL,
    CONSTRAINT fk_student
        FOREIGN KEY(student_id)
            REFERENCES students(id)
            ON DELETE CASCADE
); 