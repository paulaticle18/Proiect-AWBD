package ro.ticle.paula.spring.project.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

import java.util.Set;

@Builder
public record StudentRequest(@NotEmpty String firstName,
                             @NotEmpty String lastName,
                             @NotEmpty @Email String email,
                             @NotEmpty Set<String> courseTitles,
                             @NotEmpty String address,
                             @NotEmpty String phoneNumber) {
}
