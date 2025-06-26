package ro.ticle.paula.spring.project.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ro.ticle.paula.spring.project.model.request.StudentProfileRequest;
import ro.ticle.paula.spring.project.model.response.StudentProfileResponse;
import ro.ticle.paula.spring.project.service.StudentProfileService;

import java.util.UUID;

@RestController
@RequestMapping("/v1/students/{studentId}/profile")
@RequiredArgsConstructor
public class StudentProfileController {

    private final StudentProfileService studentProfileService;

    @PostMapping
    public void addProfile(@PathVariable UUID studentId,
                           @Valid @RequestBody StudentProfileRequest request) {
        studentProfileService.addProfile(studentId, request);
    }

    @GetMapping
    public StudentProfileResponse getProfile(@PathVariable UUID studentId) {
        return studentProfileService.getProfile(studentId);
    }
}
