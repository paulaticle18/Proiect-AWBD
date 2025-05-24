package ro.ticle.paula.spring.project.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

public record ProfessorRequest(@NotEmpty String firstName,
                               @NotEmpty String lastName,
                               @NotEmpty @Email String email,
                               @NotEmpty String phoneNumber,
                               @NotEmpty Set<String> courseTitles,
                               @NotEmpty String department) {
}
