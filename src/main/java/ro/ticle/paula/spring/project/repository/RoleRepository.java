package ro.ticle.paula.spring.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ro.ticle.paula.spring.project.entity.Role;
import ro.ticle.paula.spring.project.model.projection.RoleProjection;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);

    boolean existsByName(String name);

    @Query("SELECT r FROM Role r")
    List<RoleProjection> findAllRoles();

    @Modifying
    @Query("""
            DELETE FROM     Role r
            WHERE r.name = :roleName
            """)
    void deleteRoleByName(String roleName);
}
