package ro.ticle.paula.spring.project.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import ro.ticle.paula.spring.project.entity.Role;

import java.util.Set;
import java.util.UUID;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserResponse(UUID id,
                           String username,
                           Boolean enabled,
                           Set<Role> roles) {
}
