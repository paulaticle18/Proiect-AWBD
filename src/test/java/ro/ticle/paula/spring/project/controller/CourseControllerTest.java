package ro.ticle.paula.spring.project.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import ro.ticle.paula.spring.project.entity.Department;
import ro.ticle.paula.spring.project.exceptions.BadRequestException;
import ro.ticle.paula.spring.project.model.request.CourseRequest;
import ro.ticle.paula.spring.project.model.response.CourseResponse;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CourseController.class)
class CourseControllerTest extends BaseControllerTest {

    @Test
    void addCourse_Success() throws Exception {
        CourseRequest request = new CourseRequest("Math 101");

        mockMvc.perform(post("/v1/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf())
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void addCourse_DuplicateTitle() throws Exception {
        CourseRequest request = new CourseRequest("Math 101");

        doThrow(new BadRequestException("Course with this title already exists"))
                .when(courseService)
                .addCourse(any(CourseRequest.class));

        mockMvc.perform(post("/v1/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf())
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getCourses_Success() throws Exception {
        Department department = Department.builder()
                .id(1L)
                .name("Computer Science")
                .build();
        CourseResponse courseResponse = new CourseResponse(1L, "Math 101", department);

        when(courseService.getCourses(0, 10))
                .thenReturn(new PageImpl<>(List.of(courseResponse)));

        mockMvc.perform(get("/v1/courses")
                        .param("page", "0")
                        .param("size", "10")
                        .with(csrf())
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void getCourse_Success() throws Exception {
        Long courseId = 1L;
        Department department = Department.builder()
                .id(1L)
                .name("Computer Science")
                .build();
        CourseResponse courseResponse = new CourseResponse(courseId, "Math 101", department);

        when(courseService.getCourse(courseId)).thenReturn(courseResponse);

        mockMvc.perform(get("/v1/courses/{id}", courseId)
                        .with(csrf())
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void getCourse_NotFound() throws Exception {
        Long courseId = 1L;
        when(courseService.getCourse(courseId))
                .thenThrow(new BadRequestException("Course not found"));

        mockMvc.perform(get("/v1/courses/{id}", courseId)
                        .with(csrf())
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteCourse_Success() throws Exception {
        Long courseId = 1L;

        mockMvc.perform(delete("/v1/courses/{id}", courseId)
                        .with(csrf())
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void deleteCourse_NotFound() throws Exception {
        Long courseId = 1L;
        doThrow(new BadRequestException("Course not found"))
                .when(courseService)
                .deleteCourse(courseId);

        mockMvc.perform(delete("/v1/courses/{id}", courseId)
                        .with(csrf())
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isBadRequest());
    }
}