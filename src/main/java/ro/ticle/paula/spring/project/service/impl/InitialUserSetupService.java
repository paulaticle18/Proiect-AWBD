package ro.ticle.paula.spring.project.service.impl;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.ticle.paula.spring.project.entity.Role;
import ro.ticle.paula.spring.project.entity.User;
import ro.ticle.paula.spring.project.repository.RoleRepository;
import ro.ticle.paula.spring.project.repository.UserRepository;

import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Profile("!test")
public class InitialUserSetupService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    @Transactional
    public void init() {
        // Create ADMIN role if it doesn't exist
        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setName("ADMIN");
                    return roleRepository.save(newRole);
                });

        // Create admin user if it doesn't exist
        Optional<User> adminUserOpt = userRepository.findByUsername("admin");
        if (adminUserOpt.isEmpty()) {
            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setPassword(passwordEncoder.encode("admin"));
            adminUser.setEnabled(true);
            adminUser.setRoles(Collections.singleton(adminRole));
            userRepository.save(adminUser);
        }
    }
}
