package ro.ticle.paula.spring.project.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import ro.ticle.paula.spring.project.exceptions.BadRequestException;
import ro.ticle.paula.spring.project.model.request.StudentRequest;
import ro.ticle.paula.spring.project.model.response.StudentResponse;

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

@WebMvcTest(StudentController.class)
class StudentControllerTest extends BaseControllerTest {

    @Test
    void addStudent_Success() throws Exception {
        StudentRequest request = new StudentRequest("John", "Doe", "john.doe@example.com",  Set.of("Math"),"New York", "555-111-333" );

        mockMvc.perform(post("/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf())
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void addStudent_InvalidEmail() throws Exception {
        StudentRequest request = new StudentRequest("John", "Doe", "invalid-email", Set.of("Math"), "New York", "555-111-333");

        mockMvc.perform(post("/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf())
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getStudents_Success() throws Exception {
        StudentResponse studentResponse = new StudentResponse(UUID.randomUUID(), "John", "Doe", "john.doe@example.com", Set.of());

        when(studentService.getStudents(0, 10))
                .thenReturn(new PageImpl<>(List.of(studentResponse)));

        mockMvc.perform(get("/v1/students")
                        .param("page", "0")
                        .param("size", "10")
                        .with(csrf())
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void getStudent_Success() throws Exception {
        UUID studentId = UUID.randomUUID();
        StudentResponse studentResponse = new StudentResponse(studentId, "John", "Doe", "john.doe@example.com", Set.of());

        when(studentService.getStudent(studentId)).thenReturn(studentResponse);

        mockMvc.perform(get("/v1/students/{id}", studentId)
                        .with(csrf())
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void getStudent_NotFound() throws Exception {
        UUID studentId = UUID.randomUUID();
        when(studentService.getStudent(studentId))
                .thenThrow(new BadRequestException("Student not found"));

        mockMvc.perform(get("/v1/students/{id}", studentId)
                        .with(csrf())
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteStudent_Success() throws Exception {
        UUID studentId = UUID.randomUUID();

        mockMvc.perform(delete("/v1/students/{id}", studentId)
                        .with(csrf())
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void deleteStudent_NotFound() throws Exception {
        UUID studentId = UUID.randomUUID();
        doThrow(new BadRequestException("Student not found"))
                .when(studentService)
                .deleteStudent(studentId);

        mockMvc.perform(delete("/v1/students/{id}", studentId)
                        .with(csrf())
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isBadRequest());
    }
} 