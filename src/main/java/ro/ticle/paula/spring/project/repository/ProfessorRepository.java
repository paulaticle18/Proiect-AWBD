package ro.ticle.paula.spring.project.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ro.ticle.paula.spring.project.entity.Professor;
import ro.ticle.paula.spring.project.model.projection.ProfessorProjection;

import java.util.UUID;

@Repository
public interface ProfessorRepository extends JpaRepository<Professor, UUID> {
    boolean existsByEmail(String email);

    Page<ProfessorProjection> findAllBy(Pageable pageable);

    @Modifying
    @Query("""
            DELETE FROM Professor p
            WHERE p.id = :id
            """)
    void deleteById(UUID id);
}
