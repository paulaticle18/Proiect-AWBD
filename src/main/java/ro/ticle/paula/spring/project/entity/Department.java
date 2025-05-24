package ro.ticle.paula.spring.project.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "departments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Department {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, name = "name")
    private String name;

    @OneToMany(mappedBy = "department", cascade = CascadeType.PERSIST)
    @Builder.Default
    private Set<Professor> professors = new HashSet<>();

    @OneToMany(mappedBy = "department", cascade = CascadeType.PERSIST)
    @Builder.Default
    private Set<Course> courses = new HashSet<>();

}
