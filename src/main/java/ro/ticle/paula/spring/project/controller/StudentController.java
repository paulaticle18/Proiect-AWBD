package ro.ticle.paula.spring.project.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import ro.ticle.paula.spring.project.model.request.StudentRequest;
import ro.ticle.paula.spring.project.model.response.StudentResponse;
import ro.ticle.paula.spring.project.service.StudentService;

import java.util.UUID;

@RestController
@RequestMapping("/v1/students")
@RequiredArgsConstructor
public class StudentController {

     private final StudentService studentService;

     @PostMapping
     public void addStudent(@Valid @RequestBody StudentRequest studentRequest) {
         studentService.addStudent(studentRequest);
     }

     @GetMapping
     public Page<StudentResponse> getStudents(@RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "10") int size) {
         return studentService.getStudents(page, size);
     }

     @GetMapping("/{id}")
     public StudentResponse getStudent(@Valid @PathVariable UUID id) {
         return studentService.getStudent(id);
     }

     @DeleteMapping("/{id}")
     public void deleteStudent(@PathVariable UUID id) {
         studentService.deleteStudent(id);
     }

}
