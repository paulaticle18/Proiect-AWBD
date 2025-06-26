package ro.ticle.paula.spring.project.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "students")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false, name = "first_name")
    private String firstName;

    @Column(unique = true, nullable = false, name = "last_name")
    private String lastName;

    @Email
    @Column(unique = true, nullable = false, name = "email")
    private String email;

    @ManyToMany(mappedBy = "students")
    private Set<Course> courses;

    @OneToOne(mappedBy = "student", cascade = CascadeType.ALL)
    private StudentProfile profile;

}
