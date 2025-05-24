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
import ro.ticle.paula.spring.project.exceptions.BadRequestException;
import ro.ticle.paula.spring.project.model.request.CourseRequest;
import ro.ticle.paula.spring.project.model.response.CourseResponse;
import ro.ticle.paula.spring.project.repository.CourseRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private CourseService courseService;

    @Test
    void getOrCreateCourses_ExistingCourses() {
        Course course = Course.builder()
                .id(1L)
                .title("Test Course")
                .build();

        when(courseRepository.findByTitle("Test Course")).thenReturn(Optional.of(course));

        Set<Course> result = courseService.getOrCreateCourses(Set.of("Test Course"));

        assertThat(result).hasSize(1);
        assertThat(result.iterator().next().getTitle()).isEqualTo("Test Course");
        verify(courseRepository, never()).save(any());
    }

    @Test
    void getOrCreateCourses_NewCourses() {
        Course course = Course.builder()
                .id(1L)
                .title("Test Course")
                .build();

        when(courseRepository.findByTitle("Test Course")).thenReturn(Optional.empty());
        when(courseRepository.save(any())).thenReturn(course);

        Set<Course> result = courseService.getOrCreateCourses(Set.of("Test Course"));

        assertThat(result).hasSize(1);
        assertThat(result.iterator().next().getTitle()).isEqualTo("Test Course");
        verify(courseRepository).save(any());
    }

    @Test
    void addCourse_Success() {
        CourseRequest request = new CourseRequest("Test Course");
        when(courseRepository.findByTitle("Test Course")).thenReturn(Optional.empty());

        courseService.addCourse(request);

        verify(courseRepository).save(any());
    }

    @Test
    void addCourse_DuplicateTitle() {
        CourseRequest request = new CourseRequest("Test Course");
        when(courseRepository.findByTitle("Test Course")).thenReturn(Optional.of(new Course()));

        assertThatThrownBy(() -> courseService.addCourse(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Course with this title already exists");

        verify(courseRepository, never()).save(any());
    }

    @Test
    void getCourses_Success() {
        Department department = Department.builder()
                .id(1L)
                .name("Computer Science")
                .build();
        Course course = Course.builder()
                .id(1L)
                .title("Test Course")
                .department(department)
                .build();
        Page<Course> coursePage = new PageImpl<>(List.of(course));

        when(courseRepository.findAll(any(PageRequest.class))).thenReturn(coursePage);

        Page<CourseResponse> result = courseService.getCourses(0, 10);

        assertThat(result.getContent()).hasSize(1);
        CourseResponse response = result.getContent().get(0);
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.title()).isEqualTo("Test Course");
        assertThat(response.department()).isEqualTo(department);
    }

    @Test
    void getCourse_Success() {
        Department department = Department.builder()
                .id(1L)
                .name("Computer Science")
                .build();
        Course course = Course.builder()
                .id(1L)
                .title("Test Course")
                .department(department)
                .build();

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        CourseResponse result = courseService.getCourse(1L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.title()).isEqualTo("Test Course");
        assertThat(result.department()).isEqualTo(department);
    }

    @Test
    void getCourse_NotFound() {
        when(courseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> courseService.getCourse(1L))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Course not found with id 1");
    }

    @Test
    void deleteCourse_Success() {
        when(courseRepository.existsById(1L)).thenReturn(true);

        courseService.deleteCourse(1L);

        verify(courseRepository).deleteById(1L);
    }

    @Test
    void deleteCourse_NotFound() {
        when(courseRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> courseService.deleteCourse(1L))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Course does not exist");

        verify(courseRepository, never()).deleteById(any());
    }
} 