package ro.ticle.paula.spring.project.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import ro.ticle.paula.spring.project.exceptions.BadRequestException;
import ro.ticle.paula.spring.project.model.request.RoleRequest;
import ro.ticle.paula.spring.project.model.response.RoleResponse;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RoleController.class)
class RoleControllerTest extends BaseControllerTest {

    @Test
    void createRole_Success() throws Exception {
        RoleRequest request = new RoleRequest("TEST_ROLE");
        RoleResponse response = new RoleResponse(1L, "TEST_ROLE");

        when(roleService.createRole(any(RoleRequest.class))).thenReturn(response);

        mockMvc.perform(post("/v1/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf())
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void createRole_DuplicateName() throws Exception {
        RoleRequest request = new RoleRequest("TEST_ROLE");

        doThrow(new BadRequestException("Role with this name already exists"))
                .when(roleService)
                .createRole(any(RoleRequest.class));

        mockMvc.perform(post("/v1/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf())
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllRoles_Success() throws Exception {
        RoleResponse roleResponse = new RoleResponse(1L, "TEST_ROLE");

        when(roleService.getAllRoles()).thenReturn(List.of(roleResponse));

        mockMvc.perform(get("/v1/roles")
                        .with(csrf())
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
    }
} 