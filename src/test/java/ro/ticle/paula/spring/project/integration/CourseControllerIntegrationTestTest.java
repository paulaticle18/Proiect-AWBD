package ro.ticle.paula.spring.project.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.PageRequest;
import ro.ticle.paula.spring.project.entity.Course;
import ro.ticle.paula.spring.project.helper.mother.CourseMother;
import ro.ticle.paula.spring.project.repository.CourseRepository;
import ro.ticle.paula.spring.project.service.CourseService;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CourseControllerIntegrationTestTest extends BaseControllerIntegrationTest {

    @Autowired
    private CourseService courseService;
    @Autowired
    private CourseRepository courseRepository;

    @BeforeEach
    void setUp() {
        courseRepository.deleteAll();
    }

    @Test
    void addCourse() throws Exception {
        String courseJson = new String(
                new ClassPathResource("json/request/course-request.json").getInputStream().readAllBytes(),
                StandardCharsets.UTF_8
        );

        mockMvc.perform(post("/v1/courses")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(courseJson)
                        .with(csrf())
                        .with(user("testuser").roles("ADMIN")))
                .andDo(log())
                .andExpect(status().isOk());

        assertThat(courseRepository.findByTitle("Test Course")).isPresent();
    }

    @Test
    void addCourseWithDuplicateTitle() throws Exception {
        Course course = CourseMother.getCourse();
        courseRepository.save(course);

        String courseJson = new String(
                new ClassPathResource("json/request/course-request.json").getInputStream().readAllBytes(),
                StandardCharsets.UTF_8
        );

        mockMvc.perform(post("/v1/courses")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(courseJson)
                        .with(csrf())
                        .with(user("testuser").roles("ADMIN")))
                .andDo(log())
                .andExpect(status().isBadRequest());
    }

    @Test
    void getCourses() throws Exception {
        Course course = CourseMother.getCourse();
        courseRepository.save(course);

        mockMvc.perform(get("/v1/courses")
                        .param("page", "0")
                        .param("size", "10")
                        .with(csrf())
                        .with(user("testuser").roles("ADMIN")))
                .andDo(log())
                .andExpect(status().isOk());

        assertThat(courseRepository.findAll(PageRequest.of(0, 10))).isNotNull();
    }

    @Test
    void getCourse() throws Exception {
        Course course = CourseMother.getCourse();
        courseRepository.save(course);

        mockMvc.perform(get("/v1/courses/{id}", course.getId())
                        .with(csrf())
                        .with(user("testuser").roles("ADMIN")))
                .andDo(log())
                .andExpect(status().isOk());
    }

    @Test
    void getCourseNotFound() throws Exception {
        mockMvc.perform(get("/v1/courses/{id}", 999L)
                        .with(csrf())
                        .with(user("testuser").roles("ADMIN")))
                .andDo(log())
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteCourse() throws Exception {
        Course course = CourseMother.getCourse();
        courseRepository.save(course);

        mockMvc.perform(delete("/v1/courses/{id}", course.getId())
                        .with(csrf())
                        .with(user("testuser").roles("ADMIN")))
                .andDo(log())
                .andExpect(status().isOk());

        assertThat(courseRepository.existsById(course.getId())).isFalse();
    }

    @Test
    void deleteCourseNotFound() throws Exception {
        mockMvc.perform(delete("/v1/courses/{id}", 999L)
                        .with(csrf())
                        .with(user("testuser").roles("ADMIN")))
                .andDo(log())
                .andExpect(status().isBadRequest());
    }
} 