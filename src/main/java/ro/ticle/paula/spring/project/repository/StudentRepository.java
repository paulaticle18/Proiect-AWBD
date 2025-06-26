package ro.ticle.paula.spring.project.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ro.ticle.paula.spring.project.entity.Student;
import ro.ticle.paula.spring.project.model.projection.StudentProjection;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudentRepository extends JpaRepository<Student, UUID> {
    boolean existsByEmail(String email);

    Page<StudentProjection> findAllBy(Pageable pageable);

    Optional<StudentProjection> findStudentById(UUID id);

    @Modifying
    @Query("""
            DELETE FROM  Student s
            WHERE s.id = :id
            """)
    void deleteById(UUID id);
}
