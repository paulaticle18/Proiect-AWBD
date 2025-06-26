package ro.ticle.paula.spring.project.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import ro.ticle.paula.spring.project.entity.Department;
import ro.ticle.paula.spring.project.exceptions.BadRequestException;
import ro.ticle.paula.spring.project.model.request.ProfessorRequest;
import ro.ticle.paula.spring.project.model.request.ProfessorUpdateRequest;
import ro.ticle.paula.spring.project.model.response.ProfessorResponse;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProfessorController.class)
class ProfessorControllerTest extends BaseControllerTest {

    @Test
    void addProfessor_Success() throws Exception {
        ProfessorRequest request = new ProfessorRequest("John", "Doe", "john.doe@example.com", "1234567890", Set.of("Math"), "Computer Science");

        mockMvc.perform(post("/v1/professors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf())
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void addProfessor_InvalidEmail() throws Exception {
        ProfessorRequest request = new ProfessorRequest("John", "Doe", "invalid-email", "1234567890", Set.of("Math"), "Computer Science");

        mockMvc.perform(post("/v1/professors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf())
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getProfessors_Success() throws Exception {
        Department department = Department.builder()
                .id(1L)
                .name("Computer Science")
                .build();
        ProfessorResponse professorResponse = new ProfessorResponse(UUID.randomUUID(), "John", "Doe", "john.doe@example.com", "1234567890", department);

        when(professorService.getProfessors(0, 10))
                .thenReturn(new PageImpl<>(List.of(professorResponse)));

        mockMvc.perform(get("/v1/professors")
                        .param("page", "0")
                        .param("size", "10")
                        .with(csrf())
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void getProfessor_Success() throws Exception {
        UUID professorId = UUID.randomUUID();
        Department department = Department.builder()
                .id(1L)
                .name("Computer Science")
                .build();
        ProfessorResponse professorResponse = new ProfessorResponse(professorId, "John", "Doe", "john.doe@example.com", "1234567890", department);

        when(professorService.getProfessor(professorId)).thenReturn(professorResponse);

        mockMvc.perform(get("/v1/professors/{id}", professorId)
                        .with(csrf())
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void getProfessor_NotFound() throws Exception {
        UUID professorId = UUID.randomUUID();
        when(professorService.getProfessor(professorId))
                .thenThrow(new BadRequestException("Professor not found"));

        mockMvc.perform(get("/v1/professors/{id}", professorId)
                        .with(csrf())
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateProfessor_Success() throws Exception {
        UUID professorId = UUID.randomUUID();
        Department department = Department.builder()
                .id(1L)
                .name("Computer Science")
                .build();
        ProfessorUpdateRequest request = new ProfessorUpdateRequest("john.doe@example.com", "1234567890", department);

        mockMvc.perform(patch("/v1/professors/{id}", professorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf())
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void updateProfessor_NotFound() throws Exception {
        UUID professorId = UUID.randomUUID();
        Department department = Department.builder()
                .id(1L)
                .name("Computer Science")
                .build();
        ProfessorUpdateRequest request = new ProfessorUpdateRequest("john.doe@example.com", "1234567890", department);

        doThrow(new BadRequestException("Professor not found"))
                .when(professorService)
                .updateProfessorDetails(any(), any());

        mockMvc.perform(patch("/v1/professors/{id}", professorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf())
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteProfessor_Success() throws Exception {
        UUID professorId = UUID.randomUUID();

        mockMvc.perform(delete("/v1/professors/{id}", professorId)
                        .with(csrf())
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void deleteProfessor_NotFound() throws Exception {
        UUID professorId = UUID.randomUUID();
        doThrow(new BadRequestException("Professor not found"))
                .when(professorService)
                .deleteProfessor(professorId);

        mockMvc.perform(delete("/v1/professors/{id}", professorId)
                        .with(csrf())
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isBadRequest());
    }
} 