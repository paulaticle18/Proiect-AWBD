package ro.ticle.paula.spring.project.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;
import ro.ticle.paula.spring.project.entity.Student;
import ro.ticle.paula.spring.project.helper.mother.StudentMother;
import ro.ticle.paula.spring.project.repository.CourseRepository;
import ro.ticle.paula.spring.project.repository.StudentRepository;
import ro.ticle.paula.spring.project.service.StudentService;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class StudentControllerIntegrationTestTest extends BaseControllerIntegrationTest {

    @Autowired
    private StudentService studentService;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private CourseRepository courseRepository;

    @BeforeEach
    void setUp() {
        courseRepository.deleteAll();
        studentRepository.deleteAll();
    }

    @Test
    void addStudent() throws Exception {
        String studentJson = new String(
                new ClassPathResource("json/request/student-request.json").getInputStream().readAllBytes(),
                StandardCharsets.UTF_8
        );

        mockMvc.perform(post("/v1/students")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(studentJson)
                        .with(csrf())
                        .with(user("testuser").roles("ADMIN")))
                .andDo(log())
                .andExpect(status().isOk());

        assertThat(studentRepository.existsByEmail("test.student@mail.com")).isTrue();
    }

    @Test
    void addStudentWithInvalidEmail() throws Exception {
        String studentJson = new String(
                new ClassPathResource("json/request/student-request-invalid-email.json").getInputStream().readAllBytes(),
                StandardCharsets.UTF_8
        );

        mockMvc.perform(post("/v1/students")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(studentJson)
                        .with(csrf())
                        .with(user("testuser").roles("ADMIN")))
                .andDo(log())
                .andExpect(status().isBadRequest());
    }

    @Test
    void addStudentWithSameEmail() throws Exception {
        String studentJson = new String(
                new ClassPathResource("json/request/student-request.json").getInputStream().readAllBytes(),
                StandardCharsets.UTF_8
        );
        studentRepository.save(StudentMother.getStudent());

        mockMvc.perform(post("/v1/students")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(studentJson)
                        .with(csrf())
                        .with(user("testuser").roles("ADMIN")))
                .andDo(log())
                .andExpect(status().isBadRequest());

        assertThat(studentRepository.existsByEmail("test.student@mail.com")).isTrue();
    }

    @Test
    void getStudents() throws Exception {
        Student student = StudentMother.getStudent();
        studentRepository.save(student);

        mockMvc.perform(get("/v1/students")
                        .with(csrf())
                        .with(user("testuser").roles("ADMIN")))
                .andDo(log())
                .andExpect(status().isOk());

        assertThat(studentRepository.findAllBy(PageRequest.of(0, 10))).isNotNull();
    }

    @Test
    void getStudentById() throws Exception {
        Student student = StudentMother.getStudent();
        studentRepository.save(student);

        mockMvc.perform(get("/v1/students/{id}", student.getId())
                        .with(csrf())
                        .with(user("testuser").roles("ADMIN")))
                .andDo(log())
                .andExpect(status().isOk());
    }

    @Test
    void getStudentByIdNotFound() throws Exception {
        mockMvc.perform(get("/v1/students/{id}", "00000000-0000-0000-0000-000000000000")
                        .with(csrf())
                        .with(user("testuser").roles("ADMIN")))
                .andDo(log())
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteStudent() throws Exception {
        Student student = StudentMother.getStudent();
        studentRepository.save(student);

        mockMvc.perform(delete("/v1/students/{id}", student.getId())
                        .with(csrf())
                        .with(user("testuser").roles("ADMIN")))
                .andDo(log())
                .andExpect(status().isOk());

        assertThat(studentRepository.existsById(student.getId())).isFalse();
    }

    @Test
    void deleteStudentNotFound() throws Exception {
        mockMvc.perform(delete("/v1/students/{id}", "00000000-0000-0000-0000-000000000000")
                        .with(csrf())
                        .with(user("testuser").roles("ADMIN")))
                .andDo(log())
                .andExpect(status().isBadRequest());
    }
} 