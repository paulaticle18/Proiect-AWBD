package ro.ticle.paula.spring.project.model.projection;

import ro.ticle.paula.spring.project.entity.Role;

import java.util.Set;
import java.util.UUID;

public interface UserProjection {
    UUID getId();

    String getUsername();
    boolean getEnabled();

    Set<Role> getRoles();
}
