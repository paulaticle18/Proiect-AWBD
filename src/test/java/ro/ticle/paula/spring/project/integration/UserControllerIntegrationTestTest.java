package ro.ticle.paula.spring.project.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.web.servlet.MockMvc;
import ro.ticle.paula.spring.project.entity.Role;
import ro.ticle.paula.spring.project.entity.User;
import ro.ticle.paula.spring.project.repository.RoleRepository;
import ro.ticle.paula.spring.project.repository.UserRepository;
import ro.ticle.paula.spring.project.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerIntegrationTestTest extends BaseControllerIntegrationTest {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
        roleRepository.save(Role.builder()
                .name("TEST_ROLE")
                .users(new HashSet<>())
                .build());
    }

    @Test
    void registerUser() throws Exception {
        String userJson = new String(
                new ClassPathResource("json/request/user-request.json").getInputStream().readAllBytes(),
                StandardCharsets.UTF_8
        );

        mockMvc.perform(post("/v1/users/register")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(userJson)
                        .with(csrf())
                        .with(user("testuser").roles("ADMIN")))
                .andDo(log())
                .andExpect(status().isOk());

        assertThat(userRepository.findByUsername("testuser")).isPresent();
    }

    @Test
    void registerUserWithNonExistentRole() throws Exception {
        roleRepository.deleteAll();

        String userJson = new String(
                new ClassPathResource("json/request/user-request.json").getInputStream().readAllBytes(),
                StandardCharsets.UTF_8
        );

        mockMvc.perform(post("/v1/users/register")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(userJson)
                        .with(csrf())
                        .with(user("testuser").roles("ADMIN")))
                .andDo(log())
                .andExpect(status().isBadRequest());
    }

    @Test
    void assignRole() throws Exception {
        Role newRole = roleRepository.save(Role.builder()
                .name("NEW_ROLE")
                .users(new HashSet<>())
                .build());

        String userJson = new String(
                new ClassPathResource("json/request/user-request.json").getInputStream().readAllBytes(),
                StandardCharsets.UTF_8
        );

        mockMvc.perform(post("/v1/users/register")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(userJson)
                        .with(csrf())
                        .with(user("testuser").roles("ADMIN")))
                .andDo(log())
                .andExpect(status().isOk());

        mockMvc.perform(post("/v1/users/{username}/roles", "testuser")
                        .param("role", "NEW_ROLE")
                        .with(csrf())
                        .with(user("testuser").roles("ADMIN")))
                .andDo(log())
                .andExpect(status().isOk());

        User user = userRepository.findByUsername("testuser").orElseThrow();
        assertThat(user.getRoles().stream().anyMatch(role -> role.getName().equals("NEW_ROLE"))).isTrue();
    }

    @Test
    void removeRole() throws Exception {
        String userJson = new String(
                new ClassPathResource("json/request/user-request.json").getInputStream().readAllBytes(),
                StandardCharsets.UTF_8
        );

        mockMvc.perform(post("/v1/users/register")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(userJson)
                        .with(csrf())
                        .with(user("testuser").roles("ADMIN")))
                .andDo(log())
                .andExpect(status().isOk());

        mockMvc.perform(delete("/v1/users/{username}/roles", "testuser")
                        .param("role", "TEST_ROLE")
                        .with(csrf())
                        .with(user("testuser").roles("ADMIN")))
                .andDo(log())
                .andExpect(status().isOk());

        User user = userRepository.findByUsername("testuser").orElseThrow();
        assertThat(user.getRoles().stream().noneMatch(role -> role.getName().equals("TEST_ROLE"))).isTrue();
    }

    @Test
    void getUser() throws Exception {
        String userJson = new String(
                new ClassPathResource("json/request/user-request.json").getInputStream().readAllBytes(),
                StandardCharsets.UTF_8
        );

        mockMvc.perform(post("/v1/users/register")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(userJson)
                        .with(csrf())
                        .with(user("testuser").roles("ADMIN")))
                .andDo(log())
                .andExpect(status().isOk());

        User user = userRepository.findByUsername("testuser").orElseThrow();

        mockMvc.perform(get("/v1/users/{id}", user.getId())
                        .with(csrf())
                        .with(user("testuser").roles("ADMIN")))
                .andDo(log())
                .andExpect(status().isOk());
    }

    @Test
    void getUserNotFound() throws Exception {
        mockMvc.perform(get("/v1/users/{id}", UUID.randomUUID())
                        .with(csrf())
                        .with(user("testuser").roles("ADMIN")))
                .andDo(log())
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllUsers() throws Exception {
        String userJson = new String(
                new ClassPathResource("json/request/user-request.json").getInputStream().readAllBytes(),
                StandardCharsets.UTF_8
        );

        mockMvc.perform(post("/v1/users/register")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(userJson)
                        .with(csrf())
                        .with(user("testuser").roles("ADMIN")))
                .andDo(log())
                .andExpect(status().isOk());

        mockMvc.perform(get("/v1/users")
                        .param("page", "0")
                        .param("size", "10")
                        .with(csrf())
                        .with(user("testuser").roles("ADMIN")))
                .andDo(log())
                .andExpect(status().isOk());
    }

    @Test
    void deleteUser() throws Exception {
        String userJson = new String(
                new ClassPathResource("json/request/user-request.json").getInputStream().readAllBytes(),
                StandardCharsets.UTF_8
        );

        mockMvc.perform(post("/v1/users/register")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(userJson)
                        .with(csrf())
                        .with(user("testuser").roles("ADMIN")))
                .andDo(log())
                .andExpect(status().isOk());

        User user = userRepository.findByUsername("testuser").orElseThrow();

        mockMvc.perform(delete("/v1/users/{id}", user.getId())
                        .with(csrf())
                        .with(user("testuser").roles("ADMIN")))
                .andDo(log())
                .andExpect(status().isOk());

        assertThat(userRepository.existsById(user.getId())).isFalse();
    }

    @Test
    void deleteUserNotFound() throws Exception {
        mockMvc.perform(delete("/v1/users/{id}", UUID.randomUUID())
                        .with(csrf())
                        .with(user("testuser").roles("ADMIN")))
                .andDo(log())
                .andExpect(status().isBadRequest());
    }
} 