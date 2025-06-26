package ro.ticle.paula.spring.project.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ro.ticle.paula.spring.project.exceptions.BadRequestException;
import ro.ticle.paula.spring.project.model.request.StudentProfileRequest;
import ro.ticle.paula.spring.project.model.response.StudentProfileResponse;
import ro.ticle.paula.spring.project.service.StudentProfileService;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StudentProfileController.class)
class StudentProfileControllerTest extends BaseControllerTest {

    @Test
    void addProfile_Success() throws Exception {
        UUID studentId = UUID.randomUUID();
        StudentProfileRequest request = new StudentProfileRequest("123 Main St", "555-1234");

        mockMvc.perform(post("/v1/students/{studentId}/profile", studentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf())
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void addProfile_StudentNotFound() throws Exception {
        UUID studentId = UUID.randomUUID();
        StudentProfileRequest request = new StudentProfileRequest("123 Main St", "555-1234");

        doThrow(new BadRequestException("Student not found"))
                .when(studentProfileService)
                .addProfile(eq(studentId), any(StudentProfileRequest.class));

        mockMvc.perform(post("/v1/students/{studentId}/profile", studentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf())
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addProfile_ProfileAlreadyExists() throws Exception {
        UUID studentId = UUID.randomUUID();
        StudentProfileRequest request = new StudentProfileRequest("123 Main St", "555-1234");

        doThrow(new BadRequestException("Profile already exists for this student"))
                .when(studentProfileService)
                .addProfile(eq(studentId), any(StudentProfileRequest.class));

        mockMvc.perform(post("/v1/students/{studentId}/profile", studentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf())
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addProfile_InvalidRequest() throws Exception {
        UUID studentId = UUID.randomUUID();
        StudentProfileRequest request = new StudentProfileRequest("", "");

        mockMvc.perform(post("/v1/students/{studentId}/profile", studentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf())
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getProfile_Success() throws Exception {
        UUID studentId = UUID.randomUUID();
        UUID profileId = UUID.randomUUID();
        StudentProfileResponse response = new StudentProfileResponse(profileId, "123 Main St", "555-1234", studentId);

        when(studentProfileService.getProfile(studentId)).thenReturn(response);

        mockMvc.perform(get("/v1/students/{studentId}/profile", studentId)
                        .with(csrf())
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(profileId.toString()))
                .andExpect(jsonPath("$.address").value("123 Main St"))
                .andExpect(jsonPath("$.phoneNumber").value("555-1234"))
                .andExpect(jsonPath("$.studentId").value(studentId.toString()));
    }

    @Test
    void getProfile_NotFound() throws Exception {
        UUID studentId = UUID.randomUUID();

        when(studentProfileService.getProfile(studentId))
                .thenThrow(new BadRequestException("Profile not found for student " + studentId));

        mockMvc.perform(get("/v1/students/{studentId}/profile", studentId)
                        .with(csrf())
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isBadRequest());
    }
}
