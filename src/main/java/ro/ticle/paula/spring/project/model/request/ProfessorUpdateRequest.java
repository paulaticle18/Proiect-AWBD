package ro.ticle.paula.spring.project.model.request;

import ro.ticle.paula.spring.project.entity.Department;

public record ProfessorUpdateRequest(String email,
                                     String phoneNumber,
                                     Department department) {
}
