package ro.ticle.paula.spring.project.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ro.ticle.paula.spring.project.entity.Course;
import ro.ticle.paula.spring.project.exceptions.BadRequestException;
import ro.ticle.paula.spring.project.model.request.CourseRequest;
import ro.ticle.paula.spring.project.model.response.CourseResponse;
import ro.ticle.paula.spring.project.repository.CourseRepository;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;

    public Set<Course> getOrCreateCourses(Set<String> courseTitles) {
        Set<Course> courses = new HashSet<>();
        for (String title : courseTitles) {
            Optional<Course> existing = courseRepository.findByTitle(title);
            if (existing.isPresent()) {
                log.info("Course with title {} already exists", title);
                courses.add(existing.get());
            } else {
                log.info("Creating new course with title {}", title);
                Course course = new Course();
                course.setTitle(title);
                courses.add(courseRepository.save(course));
            }
        }
        return courses;
    }

    @Transactional
    public void addCourse(CourseRequest courseRequest) {
        if (courseRepository.findByTitle(courseRequest.title()).isPresent()) {
            log.error("Course with title {} already exists", courseRequest.title());
            throw new BadRequestException("Course with this title already exists");
        }
        log.info("Add course request: {}", courseRequest);
        Course course = Course.builder().title(courseRequest.title()).build();
        courseRepository.save(course);
    }

    public Page<CourseResponse> getCourses(int page, int size) {
        log.info("Get courses from page {} of size {}", page, size);
        return courseRepository.findAll(PageRequest.of(page, size))
                .map(course -> new CourseResponse(course.getId(), course.getTitle(), course.getDepartment()));
    }

    public CourseResponse getCourse(Long id) {
        log.info("Get course with id {}", id);
        return courseRepository.findById(id)
                .map(course -> new CourseResponse(course.getId(), course.getTitle(), course.getDepartment()))
                .orElseThrow(() -> new BadRequestException("Course not found with id " + id));
    }

    @Transactional
    public void deleteCourse(Long id) {
        if (!courseRepository.existsById(id)) {
            log.error("Course with id {} does not exist", id);
            throw new BadRequestException("Course does not exist");
        }
        log.info("Delete course with id {}", id);
        courseRepository.deleteById(id);
        log.info("Course with id {} deleted", id);
    }
}
