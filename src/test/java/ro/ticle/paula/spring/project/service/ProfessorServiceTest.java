package ro.ticle.paula.spring.project.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ro.ticle.paula.spring.project.entity.Course;
import ro.ticle.paula.spring.project.entity.Department;
import ro.ticle.paula.spring.project.entity.Professor;
import ro.ticle.paula.spring.project.exceptions.BadRequestException;
import ro.ticle.paula.spring.project.model.projection.ProfessorProjection;
import ro.ticle.paula.spring.project.model.request.ProfessorRequest;
import ro.ticle.paula.spring.project.model.request.ProfessorUpdateRequest;
import ro.ticle.paula.spring.project.model.response.ProfessorResponse;
import ro.ticle.paula.spring.project.repository.DepartmentRepository;
import ro.ticle.paula.spring.project.repository.ProfessorRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfessorServiceTest {

    @Mock
    private ProfessorRepository professorRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private ProfessorService professorService;

    @Test
    void addProfessor_Success() {
        Department department = Department.builder()
                .id(1L)
                .name("Computer Science")
                .build();
        ProfessorRequest request = new ProfessorRequest("John", "Doe", "john.doe@example.com", "1234567890", Set.of("Math"), "Computer Science");

        when(professorRepository.existsByEmail(request.email())).thenReturn(false);
        when(departmentRepository.findByName(request.department())).thenReturn(Optional.of(department));

        professorService.addProfessor(request);

        verify(professorRepository).save(any());
    }

    @Test
    void addProfessor_DuplicateEmail() {
        ProfessorRequest request = new ProfessorRequest("John", "Doe", "john.doe@example.com", "1234567890", Set.of("Math"), "Computer Science");

        when(professorRepository.existsByEmail(request.email())).thenReturn(true);

        assertThatThrownBy(() -> professorService.addProfessor(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Professor with this email already exists");

        verify(professorRepository, never()).save(any());
    }

    @Test
    void addProfessor_DepartmentNotFound() {
        ProfessorRequest request = new ProfessorRequest("John", "Doe", "john.doe@example.com", "1234567890", Set.of("Math"), "Computer Science");

        when(professorRepository.existsByEmail(request.email())).thenReturn(false);
        when(departmentRepository.findByName(request.department())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> professorService.addProfessor(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Department not found");

        verify(professorRepository, never()).save(any());
    }

    @Test
    void getProfessors_Success() {
        Department department = Department.builder()
                .id(1L)
                .name("Computer Science")
                .build();
        ProfessorProjection professor = mock(ProfessorProjection.class);
        when(professor.getId()).thenReturn(UUID.randomUUID());
        when(professor.getFirstName()).thenReturn("John");
        when(professor.getLastName()).thenReturn("Doe");
        when(professor.getEmail()).thenReturn("john.doe@example.com");
        when(professor.getPhoneNumber()).thenReturn("1234567890");
        when(professor.getDepartment()).thenReturn(department);

        Page<ProfessorProjection> professorPage = new PageImpl<>(List.of(professor));
        when(professorRepository.findAllBy(any(PageRequest.class))).thenReturn(professorPage);

        Page<ProfessorResponse> result = professorService.getProfessors(0, 10);

        assertThat(result.getContent()).hasSize(1);
        ProfessorResponse response = result.getContent().get(0);
        assertThat(response.firstName()).isEqualTo("John");
        assertThat(response.lastName()).isEqualTo("Doe");
        assertThat(response.email()).isEqualTo("john.doe@example.com");
        assertThat(response.phoneNumber()).isEqualTo("1234567890");
        assertThat(response.department()).isEqualTo(department);
    }

    @Test
    void getProfessor_Success() {
        UUID id = UUID.randomUUID();
        Department department = Department.builder()
                .id(1L)
                .name("Computer Science")
                .build();
        Professor professor = Professor.builder()
                .id(id)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phoneNumber("1234567890")
                .department(department)
                .build();

        when(professorRepository.findById(id)).thenReturn(Optional.of(professor));

        ProfessorResponse result = professorService.getProfessor(id);

        assertThat(result.id()).isEqualTo(id);
        assertThat(result.firstName()).isEqualTo("John");
        assertThat(result.lastName()).isEqualTo("Doe");
        assertThat(result.email()).isEqualTo("john.doe@example.com");
        assertThat(result.phoneNumber()).isEqualTo("1234567890");
        assertThat(result.department()).isEqualTo(department);
    }

    @Test
    void getProfessor_NotFound() {
        UUID id = UUID.randomUUID();
        when(professorRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> professorService.getProfessor(id))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Professor not found");
    }

    @Test
    void deleteProfessor_Success() {
        UUID id = UUID.randomUUID();
        when(professorRepository.existsById(id)).thenReturn(true);

        professorService.deleteProfessor(id);

        verify(professorRepository).deleteById(id);
    }

    @Test
    void deleteProfessor_NotFound() {
        UUID id = UUID.randomUUID();
        when(professorRepository.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> professorService.deleteProfessor(id))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Professor not found");

        verify(professorRepository, never()).deleteById(any());
    }

    @Test
    void updateProfessorDetails_Success() {
        UUID id = UUID.randomUUID();
        Department department = Department.builder()
                .id(1L)
                .name("Computer Science")
                .build();
        Professor professor = Professor.builder()
                .id(id)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phoneNumber("1234567890")
                .department(department)
                .build();

        ProfessorUpdateRequest request = new ProfessorUpdateRequest(
                "john.updated@example.com",
                "9876543210",
                department
        );

        when(professorRepository.findById(id)).thenReturn(Optional.of(professor));

        professorService.updateProfessorDetails(id, request);

        verify(professorRepository).save(professor);
        assertThat(professor.getEmail()).isEqualTo("john.updated@example.com");
        assertThat(professor.getPhoneNumber()).isEqualTo("9876543210");
        assertThat(professor.getDepartment()).isEqualTo(department);
    }

    @Test
    void updateProfessorDetails_NotFound() {
        UUID id = UUID.randomUUID();
        Department department = Department.builder()
                .id(1L)
                .name("Computer Science")
                .build();
        ProfessorUpdateRequest request = new ProfessorUpdateRequest(
                "john.updated@example.com",
                "9876543210",
                department
        );

        when(professorRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> professorService.updateProfessorDetails(id, request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Professor not found");

        verify(professorRepository, never()).save(any());
    }
} 