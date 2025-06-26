package ro.ticle.paula.spring.project.helper.mother;

import ro.ticle.paula.spring.project.entity.Course;

import java.util.Set;

public class CourseMother {

    public static Set<Course> courses() {
        return Set.of(course(),course2());
    }

    public static Course course() {
        return Course.builder()
                .id(1L)
                .title("Test Course")
                .department(DepartmentMother.computerScience())
                .build();
    }

    public static Course course2() {
        return Course.builder()
                .id(2L)
                .title("Test Course 2")
                .department(DepartmentMother.computerScience())
                .build();
    }

    public static Course getCourse() {
        return Course.builder()
                .title("Test Course")
                .build();
    }

}
