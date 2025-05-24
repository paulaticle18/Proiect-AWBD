package ro.ticle.paula.spring.project.helper.mother;

import ro.ticle.paula.spring.project.entity.Professor;

public class ProfessorMother {

    public static Professor getProfessor() {
        return Professor.builder()
                .firstName("Test")
                .lastName("Professor")
                .email("test.professor@mail.com")
                .phoneNumber("123456789")
                .build();
    }

}
