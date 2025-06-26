package ro.ticle.paula.spring.project.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import ro.ticle.paula.spring.project.exceptions.BadRequestException;
import ro.ticle.paula.spring.project.model.request.UserRequest;
import ro.ticle.paula.spring.project.model.response.UserResponse;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest extends BaseControllerTest {

    @Test
    void registerUser_Success() throws Exception {
        UserRequest request = new UserRequest("testuser", "password", Set.of("ROLE_USER"));

        mockMvc.perform(post("/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf())
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void registerUser_DuplicateUsername() throws Exception {
        UserRequest request = new UserRequest("testuser", "password", Set.of("ROLE_USER"));

        doThrow(new BadRequestException("User already exists"))
                .when(userService)
                .registerUser(any(UserRequest.class));

        mockMvc.perform(post("/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf())
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void assignRole_Success() throws Exception {
        mockMvc.perform(post("/v1/users/{username}/roles", "testuser")
                        .param("role", "ROLE_ADMIN")
                        .with(csrf())
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void assignRole_UserNotFound() throws Exception {
        doThrow(new BadRequestException("User not found"))
                .when(userService)
                .assignRoleToUser(eq("testuser"), any());

        mockMvc.perform(post("/v1/users/{username}/roles", "testuser")
                        .param("role", "ROLE_ADMIN")
                        .with(csrf())
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void removeRole_Success() throws Exception {
        mockMvc.perform(delete("/v1/users/{username}/roles", "testuser")
                        .param("role", "ROLE_ADMIN")
                        .with(csrf())
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void removeRole_UserNotFound() throws Exception {
        doThrow(new BadRequestException("User not found"))
                .when(userService)
                .removeRoleFromUser(eq("testuser"), any());

        mockMvc.perform(delete("/v1/users/{username}/roles", "testuser")
                        .param("role", "ROLE_ADMIN")
                        .with(csrf())
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUser_Success() throws Exception {
        UUID userId = UUID.randomUUID();
        UserResponse userResponse = new UserResponse(userId, "testuser", true, Set.of());

        when(userService.findUserById(userId)).thenReturn(userResponse);

        mockMvc.perform(get("/v1/users/{id}", userId)
                        .with(csrf())
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void getUser_NotFound() throws Exception {
        UUID userId = UUID.randomUUID();
        when(userService.findUserById(userId))
                .thenThrow(new BadRequestException("User not found"));

        mockMvc.perform(get("/v1/users/{id}", userId)
                        .with(csrf())
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllUsers_Success() throws Exception {
        UserResponse userResponse = new UserResponse(UUID.randomUUID(), "testuser", true, Set.of());

        when(userService.getAllUsers(0, 10))
                .thenReturn(new PageImpl<>(List.of(userResponse)));

        mockMvc.perform(get("/v1/users")
                        .param("page", "0")
                        .param("size", "10")
                        .with(csrf())
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void deleteUser_Success() throws Exception {
        UUID userId = UUID.randomUUID();

        mockMvc.perform(delete("/v1/users/{id}", userId)
                        .with(csrf())
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void deleteUser_NotFound() throws Exception {
        UUID userId = UUID.randomUUID();
        doThrow(new BadRequestException("User not found"))
                .when(userService)
                .deleteUser(userId);

        mockMvc.perform(delete("/v1/users/{id}", userId)
                        .with(csrf())
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isBadRequest());
    }
} 