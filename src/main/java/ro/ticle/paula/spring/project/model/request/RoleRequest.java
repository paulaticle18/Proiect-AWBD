package ro.ticle.paula.spring.project.model.request;

import jakarta.validation.constraints.NotEmpty;

public record RoleRequest(@NotEmpty String name) {
}
