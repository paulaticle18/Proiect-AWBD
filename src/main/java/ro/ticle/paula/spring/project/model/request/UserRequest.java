package ro.ticle.paula.spring.project.model.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

public record UserRequest(@NotEmpty String username,
                          @NotEmpty String password,
                          Set<String> roles) {
}
