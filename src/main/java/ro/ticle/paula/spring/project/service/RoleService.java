package ro.ticle.paula.spring.project.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ro.ticle.paula.spring.project.model.response.RoleResponse;
import ro.ticle.paula.spring.project.entity.Role;
import ro.ticle.paula.spring.project.exceptions.BadRequestException;
import ro.ticle.paula.spring.project.model.request.RoleRequest;
import ro.ticle.paula.spring.project.repository.RoleRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    @Transactional
    public RoleResponse createRole(RoleRequest roleRequest) {
        if (roleRepository.existsByName(roleRequest.name())) {
            log.error("Role with name {} already exists", roleRequest.name());
            throw new BadRequestException("Role with name " + roleRequest.name() + " already exists");
        }
        Role savedRole = roleRepository.save(
                Role.builder()
                        .name(roleRequest.name())
                        .build()
        );
        return RoleResponse.builder()
                .id(savedRole.getId())
                .name(savedRole.getName())
                .build();
    }

    public List<RoleResponse> getAllRoles() {
        log.info("Fetching all roles");
        return roleRepository.findAllRoles()
                .stream()
                .map(roleProjection -> RoleResponse.builder()
                        .id(roleProjection.getId())
                        .name(roleProjection.getName())
                        .build())
                .toList();
    }

    @Transactional
    public void deleteRoleByName(String roleName) {
        roleRepository.findByName(roleName)
                .ifPresentOrElse(role -> roleRepository.deleteRoleByName(roleName),
                        () -> {
                            throw new BadRequestException("Role with name " + roleName + " does not exist");
                        }
                );
        log.info("Role with name {} deleted", roleName);
    }

}
