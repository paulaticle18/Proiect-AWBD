package ro.ticle.paula.spring.project.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ro.ticle.paula.spring.project.model.response.StudentResponse;
import ro.ticle.paula.spring.project.entity.Course;
import ro.ticle.paula.spring.project.entity.Student;
import ro.ticle.paula.spring.project.entity.StudentProfile;
import ro.ticle.paula.spring.project.exceptions.BadRequestException;
import ro.ticle.paula.spring.project.model.projection.StudentProjection;
import ro.ticle.paula.spring.project.model.request.StudentRequest;
import ro.ticle.paula.spring.project.repository.StudentRepository;

import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final CourseService courseService;

    @Transactional
    public void addStudent(StudentRequest studentRequest) {
        if (studentRepository.existsByEmail(studentRequest.email())) {
            log.error("Student with email {} already exists", studentRequest.email());
            throw new BadRequestException("Student with email already exists");
        }
        Student newStudent = buildStudentFromRequest(studentRequest);
        studentRepository.save(newStudent);
        // Create and save profile for the new student
        StudentProfile profile = StudentProfile.builder()
                .address(studentRequest.address())
                .phoneNumber(studentRequest.phoneNumber())
                .student(newStudent)
                .build();
        newStudent.setProfile(profile);
        // Save profile (if you have a StudentProfileRepository, use it; otherwise, rely on cascade)
    }

    public Page<StudentResponse> getStudents(int page, int size) {
        log.info("Get students from page {} of size {}", page, size);
        return studentRepository.findAllBy(PageRequest.of(page, size))
                .map(StudentService::getStudentResponse);
    }

    public StudentResponse getStudent(UUID id) {
        log.info("Get student with id {}", id);
        return studentRepository.findStudentById(id)
                .map(StudentService::getStudentResponse)
                .orElseThrow(() -> new BadRequestException("Student not found with id " + id));
    }

    @Transactional
    public void deleteStudent(UUID id) {
        if (!studentRepository.existsById(id)) {
            log.error("Student with id {} does not exist", id);
            throw new BadRequestException("Student does not exist");
        }
        log.info("Delete student with id {}", id);
        studentRepository.deleteById(id);
    }

    private Student buildStudentFromRequest(StudentRequest studentRequest) {
        log.info("Creating student with first name: {}, last name: {}, email: {}",
                studentRequest.firstName(), studentRequest.lastName(), studentRequest.email());
        Set<Course> courses = courseService.getOrCreateCourses(studentRequest.courseTitles());
        Student newStudent = Student.builder()
                .firstName(studentRequest.firstName())
                .lastName(studentRequest.lastName())
                .email(studentRequest.email())
                .courses(courses)
                .build();
        courses.forEach(course -> course.getStudents().add(newStudent));
        return newStudent;
    }

    private static StudentResponse getStudentResponse(StudentProjection student) {
        return new StudentResponse(student.getId(), student.getFirstName(), student.getLastName(), student.getEmail(), student.getCourses());
    }
}
