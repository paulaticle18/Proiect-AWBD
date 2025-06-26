package ro.ticle.paula.spring.project.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ro.ticle.paula.spring.project.entity.Student;
import ro.ticle.paula.spring.project.entity.StudentProfile;
import ro.ticle.paula.spring.project.exceptions.BadRequestException;
import ro.ticle.paula.spring.project.model.request.StudentProfileRequest;
import ro.ticle.paula.spring.project.model.response.StudentProfileResponse;
import ro.ticle.paula.spring.project.repository.StudentProfileRepository;
import ro.ticle.paula.spring.project.repository.StudentRepository;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentProfileService {

    private final StudentProfileRepository studentProfileRepository;
    private final StudentRepository studentRepository;

    @Transactional
    public void addProfile(UUID studentId, StudentProfileRequest request) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new BadRequestException("Student not found"));

        if (student.getProfile() != null) {
            throw new BadRequestException("Profile already exists for this student");
        }

        StudentProfile profile = StudentProfile.builder()
                .address(request.address())
                .phoneNumber(request.phoneNumber())
                .student(student)
                .build();

        student.setProfile(profile);
        studentProfileRepository.save(profile);
        log.info("Profile created for student {}", studentId);
    }

    public StudentProfileResponse getProfile(UUID studentId) {
        StudentProfile profile = studentProfileRepository.findByStudentId(studentId)
                .orElseThrow(() -> new BadRequestException("Profile not found for student " + studentId));

        return new StudentProfileResponse(
                profile.getId(),
                profile.getAddress(),
                profile.getPhoneNumber(),
                profile.getStudent().getId()
        );
    }
}
