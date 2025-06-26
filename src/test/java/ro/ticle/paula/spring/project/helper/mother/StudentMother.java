package ro.ticle.paula.spring.project.helper.mother;

import ro.ticle.paula.spring.project.entity.Student;

import java.util.HashSet;

public class StudentMother {

    public static Student getStudent() {
        return Student.builder()
                .firstName("Test")
                .lastName("Student")
                .email("test.student@mail.com")
                .courses(new HashSet<>())
                .build();
    }
} 