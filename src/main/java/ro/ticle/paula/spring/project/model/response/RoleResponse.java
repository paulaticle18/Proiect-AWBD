package ro.ticle.paula.spring.project.model.response;

import lombok.Builder;

@Builder
public record RoleResponse(Long id,
                           String name) {
}
