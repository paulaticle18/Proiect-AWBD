package ro.ticle.paula.spring.project.model.response;

import ro.ticle.paula.spring.project.entity.Department;

import java.util.UUID;

public record ProfessorResponse(UUID id,
                                String firstName,
                                String lastName,
                                String email,
                                String phoneNumber,
                                Department department) {
    public static ProfessorResponse convertToResponse(UUID id,
                                                String firstName,
                                                String lastName,
                                                String email,
                                                String phoneNumber,
                                                Department department) {
        return new ProfessorResponse(id, firstName, lastName, email, phoneNumber, department);
    }
}
