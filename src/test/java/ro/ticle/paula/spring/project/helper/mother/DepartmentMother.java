package ro.ticle.paula.spring.project.helper.mother;

import ro.ticle.paula.spring.project.entity.Department;

public class DepartmentMother {

    public static Department computerScience() {
        return Department.builder()
                .id(1L)
                .name("Computer Science")
                .build();
    }

}
