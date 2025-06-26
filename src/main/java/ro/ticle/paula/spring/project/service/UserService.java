package ro.ticle.paula.spring.project.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ro.ticle.paula.spring.project.entity.Role;
import ro.ticle.paula.spring.project.entity.User;
import ro.ticle.paula.spring.project.exceptions.BadRequestException;
import ro.ticle.paula.spring.project.model.projection.UserProjection;
import ro.ticle.paula.spring.project.model.request.UserRequest;
import ro.ticle.paula.spring.project.model.response.UserResponse;
import ro.ticle.paula.spring.project.repository.RoleRepository;
import ro.ticle.paula.spring.project.repository.UserRepository;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public void registerUser(UserRequest userRequest) {
        Set<Role> roles = new HashSet<>();
        for (String roleName : userRequest.roles()) {
            Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new BadRequestException("Role not found: " + roleName));
            roles.add(role);
        }
        User user = User.builder()
                .username(userRequest.username())
                .password(passwordEncoder.encode(userRequest.password()))
                .enabled(true)
                .roles(roles)
                .build();
        log.info("Registering user: " + user.getUsername());

        userRepository.save(user);
    }

    @Transactional
    public void assignRoleToUser(String username, String roleName) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadRequestException("User not found"));
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new BadRequestException("Role not found"));
        log.info("Assigning role {} to user {}", role, username);
        user.getRoles().add(role);
        userRepository.save(user);
    }

    @Transactional
    public void removeRoleFromUser(String username, String roleName) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadRequestException("User not found"));
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new BadRequestException("Role not found"));
        log.info("Removing role {} from user {}", role, username);
        user.getRoles().remove(role);
        userRepository.save(user);
    }

    public UserResponse findUserById(UUID id) {
        return userRepository.findUserById(id)
                .map(UserService::buildUserResponse)
                .orElseThrow(() -> new BadRequestException("User not found"));
    }

    public Page<UserResponse> getAllUsers(int page, int size) {
        log.info("Retrieving users from page {} of size {}", page, size);
        return userRepository.findAllUsers(PageRequest.of(page, size))
                .map(UserService::buildUserResponse);
    }

    @Transactional
    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            log.error("User with id {} does not exist", id);
            throw new BadRequestException("User not found");
        }
        userRepository.deleteById(id);
        log.info("User with id {} deleted successfully", id);
    }

    private static UserResponse buildUserResponse(UserProjection userProjection) {
        return UserResponse.builder()
                .id(userProjection.getId())
                .username(userProjection.getUsername())
                .roles(userProjection.getRoles())
                .enabled(userProjection.getEnabled())
                .build();
    }

}
