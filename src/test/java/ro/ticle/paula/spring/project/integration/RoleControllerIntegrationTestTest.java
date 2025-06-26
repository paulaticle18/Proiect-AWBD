package ro.ticle.paula.spring.project.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import ro.ticle.paula.spring.project.entity.Role;
import ro.ticle.paula.spring.project.repository.RoleRepository;
import ro.ticle.paula.spring.project.service.RoleService;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RoleControllerIntegrationTestTest extends BaseControllerIntegrationTest {

    @Autowired
    private RoleService roleService;
    @Autowired
    private RoleRepository roleRepository;

    @BeforeEach
    void setUp() {
        roleRepository.deleteAll();
    }

    @Test
    void createRole() throws Exception {
        String roleJson = new String(
                new ClassPathResource("json/request/role-request.json").getInputStream().readAllBytes(),
                StandardCharsets.UTF_8
        );

        mockMvc.perform(post("/v1/roles")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(roleJson)
                        .with(csrf())
                        .with(user("testuser").roles("ADMIN")))
                .andDo(log())
                .andExpect(status().isOk());

        assertThat(roleRepository.existsByName("TEST_ROLE")).isTrue();
    }

    @Test
    void createDuplicateRole() throws Exception {
        roleRepository.save(Role.builder()
                .name("TEST_ROLE")
                .users(new HashSet<>())
                .build());

        String roleJson = new String(
                new ClassPathResource("json/request/role-request.json").getInputStream().readAllBytes(),
                StandardCharsets.UTF_8
        );

        mockMvc.perform(post("/v1/roles")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(roleJson)
                        .with(csrf())
                        .with(user("testuser").roles("ADMIN")))
                .andDo(log())
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllRoles() throws Exception {
        roleRepository.save(Role.builder()
                .name("TEST_ROLE")
                .users(new HashSet<>())
                .build());
        roleRepository.save(Role.builder()
                .name("ADMIN")
                .users(new HashSet<>())
                .build());

        mockMvc.perform(get("/v1/roles")
                        .with(csrf())
                        .with(user("testuser").roles("ADMIN")))
                .andDo(log())
                .andExpect(status().isOk());
    }

    @Test
    void deleteRole() throws Exception {
        Role role = roleRepository.save(Role.builder()
                .name("TEST_ROLE")
                .users(new HashSet<>())
                .build());

        mockMvc.perform(delete("/v1/roles/{roleName}", role.getName())
                        .with(csrf())
                        .with(user("testuser").roles("ADMIN")))
                .andDo(log())
                .andExpect(status().isOk());

        assertThat(roleRepository.existsByName("TEST_ROLE")).isFalse();
    }

    @Test
    void deleteRoleNotFound() throws Exception {
        mockMvc.perform(delete("/v1/roles/{roleName}", "NON_EXISTENT_ROLE")
                        .with(csrf())
                        .with(user("testuser").roles("ADMIN")))
                .andDo(log())
                .andExpect(status().isBadRequest());
    }
} 