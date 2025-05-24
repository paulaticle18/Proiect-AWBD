package ro.ticle.paula.spring.project.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ro.ticle.paula.spring.project.entity.Course;
import ro.ticle.paula.spring.project.entity.Department;
import ro.ticle.paula.spring.project.entity.Professor;
import ro.ticle.paula.spring.project.exceptions.BadRequestException;
import ro.ticle.paula.spring.project.model.request.ProfessorRequest;
import ro.ticle.paula.spring.project.model.request.ProfessorUpdateRequest;
import ro.ticle.paula.spring.project.model.response.ProfessorResponse;
import ro.ticle.paula.spring.project.repository.DepartmentRepository;
import ro.ticle.paula.spring.project.repository.ProfessorRepository;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfessorService {

    private static final String PROFESSOR_WITH_ID_DOES_NOT_EXIST = "Professor with id [{}] does not exist";

    private final ProfessorRepository professorRepository;
    private final DepartmentRepository departmentRepository;

    @Transactional
    public void addProfessor(ProfessorRequest professorRequest) {
        if (professorRepository.existsByEmail(professorRequest.email())) {
            log.error("Professor with email [{}] already exists", professorRequest.email());
            throw new BadRequestException("Professor with this email already exists");
        }
        Department department = departmentRepository.findByName(professorRequest.department())
                .orElseThrow(() -> new BadRequestException("Department not found"));
        log.info("Adding professor: [{}]", professorRequest.firstName().concat(" ").concat(professorRequest.lastName()));
        Professor professor = buildProfessorFromRequest(professorRequest, department);
        professorRepository.save(professor);
        log.info("Professor added successfully");
    }

    public Page<ProfessorResponse> getProfessors(int page, int size) {
        log.info("Fetching professors");
        return professorRepository.findAllBy(PageRequest.of(page, size))
                .map(professorProjection -> ProfessorResponse.convertToResponse(
                        professorProjection.getId(),
                        professorProjection.getFirstName(),
                        professorProjection.getLastName(),
                        professorProjection.getEmail(),
                        professorProjection.getPhoneNumber(),
                        professorProjection.getDepartment()
                ));
    }

    public ProfessorResponse getProfessor(UUID id) {
        log.info("Fetching professor with id: [{}]", id);
        return professorRepository.findById(id)
                .map(professor -> ProfessorResponse.convertToResponse(professor.getId(),
                        professor.getFirstName(),
                        professor.getLastName(),
                        professor.getEmail(),
                        professor.getPhoneNumber(),
                        professor.getDepartment()))
                .orElseThrow(() -> new BadRequestException("Professor not found"));
    }

    @Transactional
    public void deleteProfessor(UUID id) {
        if (!professorRepository.existsById(id)) {
            log.error(PROFESSOR_WITH_ID_DOES_NOT_EXIST, id);
            throw new BadRequestException("Professor not found");
        }
        log.info("Deleting professor with id: [{}]", id);
        professorRepository.deleteById(id);
        log.info("Professor deleted successfully");
    }

    @Transactional
    public void updateProfessorDetails(UUID id, ProfessorUpdateRequest professorRequest) {

        log.info("Updating professor with id: [{}]", id);
        professorRepository.findById(id)
                .ifPresentOrElse(professor -> {
                    professor.setEmail(professorRequest.email());
                    professor.setPhoneNumber(professorRequest.phoneNumber());
                    professor.setDepartment(professorRequest.department());
                    professorRepository.save(professor);
                }, () -> {
                    log.error(PROFESSOR_WITH_ID_DOES_NOT_EXIST, id);
                    throw new BadRequestException("Professor not found");
                });
        log.info("Professor updated successfully");
    }

    private static Professor buildProfessorFromRequest(ProfessorRequest professorRequest, Department department) {
        Set<Course> courses = professorRequest.courseTitles().stream()
                .map(title -> {
                    Course course = new Course();
                    course.setTitle(title);
                    course.setDepartment(department);
                    return course;
                })
                .collect(Collectors.toSet());

        Professor professor = Professor.builder()
                .firstName(professorRequest.firstName())
                .lastName(professorRequest.lastName())
                .email(professorRequest.email())
                .phoneNumber(professorRequest.phoneNumber())
                .courses(courses)
                .department(department)
                .build();

        courses.forEach(course -> course.setProfessor(professor));
        return professor;
    }

}
