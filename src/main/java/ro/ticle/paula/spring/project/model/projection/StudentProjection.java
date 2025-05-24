package ro.ticle.paula.spring.project.model.projection;

import ro.ticle.paula.spring.project.entity.Course;

import java.util.Set;
import java.util.UUID;

public interface StudentProjection {
    UUID getId();
    String getFirstName();
    String getLastName();
    String getEmail();
    Set<Course> getCourses();
}
