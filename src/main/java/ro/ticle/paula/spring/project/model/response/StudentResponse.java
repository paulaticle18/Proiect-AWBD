package ro.ticle.paula.spring.project.model.response;

import ro.ticle.paula.spring.project.entity.Course;

import java.util.Set;
import java.util.UUID;

public record StudentResponse(UUID id,
                              String firstName,
                              String lastName,
                              String email,
                              Set<Course> courses) {
}
