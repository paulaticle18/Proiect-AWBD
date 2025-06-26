package ro.ticle.paula.spring.project.model.projection;

import ro.ticle.paula.spring.project.entity.Department;

import java.util.UUID;

public interface ProfessorProjection {
    UUID getId();

    String getFirstName();

    String getLastName();

    String getEmail();

    String getPhoneNumber();

    Department getDepartment();
}
