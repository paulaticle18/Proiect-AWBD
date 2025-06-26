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
import ro.ticle.paula.spring.project.exceptions.BadRequestException;
import ro.ticle.paula.spring.project.model.projection.StudentProjection;
import ro.ticle.paula.spring.project.model.request.StudentRequest;
import ro.ticle.paula.spring.project.model.response.StudentResponse;
import ro.ticle.paula.spring.project.repository.StudentRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private CourseService courseService;

    @InjectMocks
    private StudentService studentService;

    @Test
    void addStudent_Success() {
        StudentRequest request = new StudentRequest("John", "Doe", "john.doe@example.com", Set.of("Math"), "New York", "555-111-333");
        Course course = Course.builder()
                .id(1L)
                .title("Math")
                .build();

        when(studentRepository.existsByEmail(request.email())).thenReturn(false);
        when(courseService.getOrCreateCourses(request.courseTitles())).thenReturn(Set.of(course));

        studentService.addStudent(request);

        verify(studentRepository).save(any());
    }

    @Test
    void addStudent_DuplicateEmail() {
        StudentRequest request = new StudentRequest("John", "Doe", "john.doe@example.com", Set.of("Math"), "New York", "555-111-333");

        when(studentRepository.existsByEmail(request.email())).thenReturn(true);

        assertThatThrownBy(() -> studentService.addStudent(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Student with email already exists");

        verify(studentRepository, never()).save(any());
    }

    @Test
    void getStudents_Success() {
        StudentProjection student = mock(StudentProjection.class);
        when(student.getId()).thenReturn(UUID.randomUUID());
        when(student.getFirstName()).thenReturn("John");
        when(student.getLastName()).thenReturn("Doe");
        when(student.getEmail()).thenReturn("john.doe@example.com");
        when(student.getCourses()).thenReturn(Set.of());

        Page<StudentProjection> studentPage = new PageImpl<>(List.of(student));
        when(studentRepository.findAllBy(any(PageRequest.class))).thenReturn(studentPage);

        Page<StudentResponse> result = studentService.getStudents(0, 10);

        assertThat(result.getContent()).hasSize(1);
        StudentResponse response = result.getContent().get(0);
        assertThat(response.firstName()).isEqualTo("John");
        assertThat(response.lastName()).isEqualTo("Doe");
        assertThat(response.email()).isEqualTo("john.doe@example.com");
        assertThat(response.courses()).isEmpty();
    }

    @Test
    void getStudent_Success() {
        UUID id = UUID.randomUUID();
        StudentProjection student = mock(StudentProjection.class);
        when(student.getId()).thenReturn(id);
        when(student.getFirstName()).thenReturn("John");
        when(student.getLastName()).thenReturn("Doe");
        when(student.getEmail()).thenReturn("john.doe@example.com");
        when(student.getCourses()).thenReturn(Set.of());

        when(studentRepository.findStudentById(id)).thenReturn(Optional.of(student));

        StudentResponse result = studentService.getStudent(id);

        assertThat(result.id()).isEqualTo(id);
        assertThat(result.firstName()).isEqualTo("John");
        assertThat(result.lastName()).isEqualTo("Doe");
        assertThat(result.email()).isEqualTo("john.doe@example.com");
        assertThat(result.courses()).isEmpty();
    }

    @Test
    void getStudent_NotFound() {
        UUID id = UUID.randomUUID();
        when(studentRepository.findStudentById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentService.getStudent(id))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Student not found with id " + id);
    }

    @Test
    void deleteStudent_Success() {
        UUID id = UUID.randomUUID();
        when(studentRepository.existsById(id)).thenReturn(true);

        studentService.deleteStudent(id);

        verify(studentRepository).deleteById(id);
    }

    @Test
    void deleteStudent_NotFound() {
        UUID id = UUID.randomUUID();
        when(studentRepository.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> studentService.deleteStudent(id))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Student does not exist");

        verify(studentRepository, never()).deleteById(any());
    }
} 