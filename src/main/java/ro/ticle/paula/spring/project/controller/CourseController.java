package ro.ticle.paula.spring.project.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import ro.ticle.paula.spring.project.model.request.CourseRequest;
import ro.ticle.paula.spring.project.model.response.CourseResponse;
import ro.ticle.paula.spring.project.service.CourseService;

@RestController
@RequestMapping("/v1/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @PostMapping
    public void addCourse(@RequestBody CourseRequest courseRequest) {
        courseService.addCourse(courseRequest);
    }

    @GetMapping
    public Page<CourseResponse> getCourses(@RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size) {
        return courseService.getCourses(page, size);
    }

    @GetMapping("/{id}")
    public CourseResponse getCourse(@PathVariable Long id) {
        return courseService.getCourse(id);
    }

    @DeleteMapping("/{id}")
    public void deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
    }
}
