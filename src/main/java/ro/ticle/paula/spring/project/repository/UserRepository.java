package ro.ticle.paula.spring.project.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ro.ticle.paula.spring.project.entity.User;
import ro.ticle.paula.spring.project.model.projection.UserProjection;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);

    Optional<UserProjection> findUserById(UUID id);

    @Query("SELECT u FROM User u")
    Page<UserProjection> findAllUsers(Pageable pageable);

    @Modifying
    @Query("""
            DELETE FROM User u
            WHERE u.id = :id
            """)
    void deleteById(UUID id);
}
