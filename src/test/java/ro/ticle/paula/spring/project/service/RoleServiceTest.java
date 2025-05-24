package ro.ticle.paula.spring.project.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ro.ticle.paula.spring.project.entity.Role;
import ro.ticle.paula.spring.project.exceptions.BadRequestException;
import ro.ticle.paula.spring.project.model.projection.RoleProjection;
import ro.ticle.paula.spring.project.model.request.RoleRequest;
import ro.ticle.paula.spring.project.model.response.RoleResponse;
import ro.ticle.paula.spring.project.repository.RoleRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService;

    @Test
    void createRole_Success() {
        RoleRequest request = new RoleRequest("ROLE_TEST");
        Role role = Role.builder()
                .id(1L)
                .name("ROLE_TEST")
                .build();

        when(roleRepository.existsByName(request.name())).thenReturn(false);
        when(roleRepository.save(any())).thenReturn(role);

        RoleResponse result = roleService.createRole(request);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("ROLE_TEST");
        verify(roleRepository).save(any());
    }

    @Test
    void createRole_DuplicateName() {
        RoleRequest request = new RoleRequest("ROLE_TEST");

        when(roleRepository.existsByName(request.name())).thenReturn(true);

        assertThatThrownBy(() -> roleService.createRole(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Role with name ROLE_TEST already exists");

        verify(roleRepository, never()).save(any());
    }

    @Test
    void getAllRoles_Success() {
        RoleProjection role = mock(RoleProjection.class);
        when(role.getId()).thenReturn(1L);
        when(role.getName()).thenReturn("ROLE_TEST");

        when(roleRepository.findAllRoles()).thenReturn(List.of(role));

        List<RoleResponse> result = roleService.getAllRoles();

        assertThat(result).hasSize(1);
        RoleResponse response = result.get(0);
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("ROLE_TEST");
    }
} 