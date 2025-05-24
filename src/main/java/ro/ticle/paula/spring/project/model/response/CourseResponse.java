package ro.ticle.paula.spring.project.model.response;

import ro.ticle.paula.spring.project.entity.Department;

public record CourseResponse(Long id,
                             String title,
                             Department department) {
} 