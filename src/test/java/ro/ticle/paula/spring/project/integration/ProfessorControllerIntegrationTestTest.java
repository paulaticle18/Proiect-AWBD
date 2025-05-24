package ro.ticle.paula.spring.project.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.PageRequest;
import ro.ticle.paula.spring.project.entity.Department;
import ro.ticle.paula.spring.project.entity.Professor;
import ro.ticle.paula.spring.project.helper.mother.ProfessorMother;
import ro.ticle.paula.spring.project.repository.CourseRepository;
import ro.ticle.paula.spring.project.repository.DepartmentRepository;
import ro.ticle.paula.spring.project.repository.ProfessorRepository;
import ro.ticle.paula.spring.project.service.ProfessorService;

import java.nio.charset.StandardCharsets;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProfessorControllerIntegrationTestTest extends BaseControllerIntegrationTest {

    @Autowired
    private ProfessorService professorService;
    @Autowired
    private ProfessorRepository professorRepository;
    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired
    private CourseRepository courseRepository;

    @BeforeEach
    void setUp() {
        courseRepository.deleteAll();
        professorRepository.deleteAll();
        departmentRepository.deleteAll();
        departmentRepository.save(new Department(null, "Computer Science", Set.of(), Set.of()));
    }

    @Test
    void addProfessor() throws Exception {
        String professorJson = new String(
                new ClassPathResource("json/request/professor-request.json").getInputStream().readAllBytes(),
                StandardCharsets.UTF_8
        );

        mockMvc.perform(post("/v1/professors")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(professorJson)
                        .with(csrf())
                        .with(user("testuser").roles("ADMIN")))
                .andDo(log())
                .andExpect(status().isOk());

        assertThat(professorRepository.existsByEmail("test.professor@mail.com")).isTrue();
    }

    @Test
    void addProfessorWithInvalidEmail() throws Exception {
        String professorJson = new String(
                new ClassPathResource("json/request/professor-request-invalid-email.json").getInputStream().readAllBytes(),
                StandardCharsets.UTF_8
        );

        mockMvc.perform(post("/v1/professors")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(professorJson)
                        .with(csrf())
                        .with(user("testuser").roles("ADMIN")))
                .andDo(log())
                .andExpect(status().isBadRequest());

        assertThat(professorRepository.existsByEmail("invalid.email")).isFalse();
    }

    @Test
    void addProfessorWithSameEmail() throws Exception {
        String professorJson = new String(
                new ClassPathResource("json/request/professor-request.json").getInputStream().readAllBytes(),
                StandardCharsets.UTF_8
        );
        professorRepository.save(ProfessorMother.getProfessor());

        mockMvc.perform(post("/v1/professors")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(professorJson)
                        .with(csrf())
                        .with(user("testuser").roles("ADMIN")))
                .andDo(log())
                .andExpect(status().isBadRequest());

        assertThat(professorRepository.existsByEmail("test.professor@mail.com")).isTrue();
    }

    @Test
    void getProfessors() throws Exception {
        Professor professor = ProfessorMother.getProfessor();
        professorRepository.save(professor);

        mockMvc.perform(get("/v1/professors")
                        .with(csrf())
                        .with(user("testuser").roles("ADMIN")))
                .andDo(log())
                .andExpect(status().isOk());

        assertThat(professorRepository.findAllBy(PageRequest.of(0, 10))).isNotNull();
    }

    @Test
    void getProfessorById() throws Exception {
        Professor professor = ProfessorMother.getProfessor();
        professorRepository.save(professor);

        mockMvc.perform(get("/v1/professors/{id}", professor.getId())
                        .with(csrf())
                        .with(user("testuser").roles("ADMIN")))
                .andDo(log())
                .andExpect(status().isOk());
    }

    @Test
    void getProfessorByIdNotFound() throws Exception {
        mockMvc.perform(get("/v1/professors/{id}", "00000000-0000-0000-0000-000000000000")
                        .with(csrf())
                        .with(user("testuser").roles("ADMIN")))
                .andDo(log())
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteProfessor() throws Exception {
        Professor professor = ProfessorMother.getProfessor();
        professorRepository.save(professor);

        mockMvc.perform(delete("/v1/professors/{id}", professor.getId())
                        .with(csrf())
                        .with(user("testuser").roles("ADMIN")))
                .andDo(log())
                .andExpect(status().isOk());

        assertThat(professorRepository.existsById(professor.getId())).isFalse();
    }

    @Test
    void deleteProfessorNotFound() throws Exception {
        mockMvc.perform(delete("/v1/professors/{id}", "00000000-0000-0000-0000-000000000000")
                        .with(csrf())
                        .with(user("testuser").roles("ADMIN")))
                .andDo(log())
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateProfessor() throws Exception {
        Department department = departmentRepository.save(new Department(null, "Computer Science", Set.of(), Set.of()));
        Professor professor = ProfessorMother.getProfessor();
        professor.setDepartment(department);
        professorRepository.save(professor);

        String updateJsonTemplate = new String(
                new ClassPathResource("json/request/professor-update-request.json").getInputStream().readAllBytes(),
                StandardCharsets.UTF_8
        );
        String updateJson = String.format(updateJsonTemplate, department.getId());

        mockMvc.perform(patch("/v1/professors/{id}", professor.getId())
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(updateJson)
                        .with(csrf())
                        .with(user("testuser").roles("ADMIN")))
                .andDo(log())
                .andExpect(status().isOk());

        Professor updatedProfessor = professorRepository.findById(professor.getId()).orElseThrow();
        assertThat(updatedProfessor.getEmail()).isEqualTo("updated.professor@mail.com");
        assertThat(updatedProfessor.getPhoneNumber()).isEqualTo("9876543210");
        assertThat(updatedProfessor.getDepartment().getId()).isEqualTo(department.getId());
    }

    @Test
    void updateProfessorNotFound() throws Exception {
        Department department = departmentRepository.save(new Department(null, "Computer Science", Set.of(), Set.of()));

        String updateJsonTemplate = new String(
                new ClassPathResource("json/request/professor-update-request.json").getInputStream().readAllBytes(),
                StandardCharsets.UTF_8
        );
        String updateJson = String.format(updateJsonTemplate, department.getId());

        mockMvc.perform(patch("/v1/professors/{id}", "00000000-0000-0000-0000-000000000000")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(updateJson)
                        .with(csrf())
                        .with(user("testuser").roles("ADMIN")))
                .andDo(log())
                .andExpect(status().isBadRequest());
    }

}
