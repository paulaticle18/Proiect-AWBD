package ro.ticle.paula.spring.project.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import ro.ticle.paula.spring.project.model.request.UserRequest;
import ro.ticle.paula.spring.project.model.response.UserResponse;
import ro.ticle.paula.spring.project.service.UserService;

import java.util.UUID;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public void registerUser(@Valid @RequestBody UserRequest userRequest) {
        userService.registerUser(userRequest);
    }

    @PostMapping("/{username}/roles")
    public void assignRole(@PathVariable String username, @RequestParam String role) {
        userService.assignRoleToUser(username, role);
    }

    @DeleteMapping("/{username}/roles")
    public void removeRole(@PathVariable String username, @RequestParam String role) {
        userService.removeRoleFromUser(username, role);
    }

    @GetMapping("/{id}")
    public UserResponse getUser(@PathVariable UUID id) {
        return userService.findUserById(id);
    }

    @GetMapping
    public Page<UserResponse> getAllUsers(@RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size) {
        return userService.getAllUsers(page, size);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
    }

}
