package ro.ticle.paula.spring.project.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ro.ticle.paula.spring.project.entity.Course;

import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    Optional<Course> findByTitle(String title);

    @Modifying
    @Query("""
            DELETE FROM Course c
            WHERE c.id = :id
            """)
    void deleteById(Long id);
}
