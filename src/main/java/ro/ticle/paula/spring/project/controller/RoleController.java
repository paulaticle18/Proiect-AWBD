package ro.ticle.paula.spring.project.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ro.ticle.paula.spring.project.model.request.RoleRequest;
import ro.ticle.paula.spring.project.model.response.RoleResponse;
import ro.ticle.paula.spring.project.service.RoleService;

import java.util.List;

@RestController
@RequestMapping("/v1/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    public RoleResponse createRole(@Valid @RequestBody RoleRequest roleRequest) {
        return roleService.createRole(roleRequest);
    }

    @GetMapping
    public List<RoleResponse> getAllRoles() {
        return roleService.getAllRoles();
    }

    @DeleteMapping("/{roleName}")
    public void deleteRole(@PathVariable String roleName) {
        roleService.deleteRoleByName(roleName);
    }

}
