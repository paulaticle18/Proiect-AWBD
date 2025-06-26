package ro.ticle.paula.spring.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ro.ticle.paula.spring.project.service.*;

@WebMvcTest
public abstract class BaseControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockitoBean
    protected UserService userService;

    @MockitoBean
    protected RoleService roleService;

    @MockitoBean
    protected StudentService studentService;

    @MockitoBean
    protected ProfessorService professorService;

    @MockitoBean
    protected CourseService courseService;

    @MockitoBean
    protected StudentProfileService studentProfileService;
} 