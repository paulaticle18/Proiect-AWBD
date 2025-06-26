package ro.ticle.paula.spring.project.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import ro.ticle.paula.spring.project.model.request.ProfessorRequest;
import ro.ticle.paula.spring.project.model.request.ProfessorUpdateRequest;
import ro.ticle.paula.spring.project.model.response.ProfessorResponse;
import ro.ticle.paula.spring.project.service.ProfessorService;

import java.util.UUID;

@RestController
@RequestMapping("/v1/professors")
@RequiredArgsConstructor
public class ProfessorController {

    private final ProfessorService professorService;

    @PostMapping
    public void addProfessor(@Valid @RequestBody ProfessorRequest professorRequest) {
        professorService.addProfessor(professorRequest);
    }

    @GetMapping
    public Page<ProfessorResponse> getProfessors(@RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size) {
        return professorService.getProfessors(page, size);
    }

    @GetMapping("/{id}")
    public ProfessorResponse getProfessor(@Valid @PathVariable UUID id) {
        return professorService.getProfessor(id);
    }

    @DeleteMapping("/{id}")
    public void deleteProfessor(@PathVariable UUID id) {
        professorService.deleteProfessor(id);
    }

    @PatchMapping("/{id}")
    public void updateProfessor(@PathVariable UUID id,
                                @RequestBody ProfessorUpdateRequest professorRequest) {
        professorService.updateProfessorDetails(id, professorRequest);
    }

}
