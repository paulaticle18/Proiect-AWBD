package ro.ticle.paula.spring.project.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ro.ticle.paula.spring.project.entity.Role;
import ro.ticle.paula.spring.project.entity.User;
import ro.ticle.paula.spring.project.exceptions.BadRequestException;
import ro.ticle.paula.spring.project.model.projection.UserProjection;
import ro.ticle.paula.spring.project.model.request.UserRequest;
import ro.ticle.paula.spring.project.model.response.UserResponse;
import ro.ticle.paula.spring.project.repository.RoleRepository;
import ro.ticle.paula.spring.project.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void registerUser_Success() {
        UserRequest request = new UserRequest("testuser", "password", Set.of("ROLE_USER"));
        Role role = Role.builder()
                .id(1L)
                .name("ROLE_USER")
                .build();

        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(role));
        when(passwordEncoder.encode("password")).thenReturn("encoded_password");

        userService.registerUser(request);

        verify(userRepository).save(any());
    }

    @Test
    void registerUser_RoleNotFound() {
        UserRequest request = new UserRequest("testuser", "password", Set.of("ROLE_INVALID"));

        when(roleRepository.findByName("ROLE_INVALID")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.registerUser(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Role not found: ROLE_INVALID");

        verify(userRepository, never()).save(any());
    }

    @Test
    void assignRoleToUser_Success() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .username("testuser")
                .password("encoded_password")
                .enabled(true)
                .roles(new java.util.HashSet<>())
                .build();
        Role role = Role.builder()
                .id(1L)
                .name("ROLE_ADMIN")
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.of(role));

        userService.assignRoleToUser("testuser", "ROLE_ADMIN");

        verify(userRepository).save(user);
        assertThat(user.getRoles()).contains(role);
    }

    @Test
    void assignRoleToUser_UserNotFound() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.assignRoleToUser("testuser", "ROLE_ADMIN"))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("User not found");

        verify(userRepository, never()).save(any());
    }

    @Test
    void assignRoleToUser_RoleNotFound() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .username("testuser")
                .password("encoded_password")
                .enabled(true)
                .roles(new java.util.HashSet<>())
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.assignRoleToUser("testuser", "ROLE_ADMIN"))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Role not found");

        verify(userRepository, never()).save(any());
    }

    @Test
    void removeRoleFromUser_Success() {
        Role role = Role.builder()
                .id(1L)
                .name("ROLE_ADMIN")
                .build();
        User user = User.builder()
                .id(UUID.randomUUID())
                .username("testuser")
                .password("encoded_password")
                .enabled(true)
                .roles(new java.util.HashSet<>(Set.of(role)))
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.of(role));

        userService.removeRoleFromUser("testuser", "ROLE_ADMIN");

        verify(userRepository).save(user);
        assertThat(user.getRoles()).doesNotContain(role);
    }

    @Test
    void removeRoleFromUser_UserNotFound() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.removeRoleFromUser("testuser", "ROLE_ADMIN"))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("User not found");

        verify(userRepository, never()).save(any());
    }

    @Test
    void removeRoleFromUser_RoleNotFound() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .username("testuser")
                .password("encoded_password")
                .enabled(true)
                .roles(new java.util.HashSet<>())
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.removeRoleFromUser("testuser", "ROLE_ADMIN"))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Role not found");

        verify(userRepository, never()).save(any());
    }

    @Test
    void findUserById_Success() {
        UUID id = UUID.randomUUID();
        UserProjection user = mock(UserProjection.class);
        when(user.getId()).thenReturn(id);
        when(user.getUsername()).thenReturn("testuser");
        when(user.getEnabled()).thenReturn(true);
        when(user.getRoles()).thenReturn(Set.of());

        when(userRepository.findUserById(id)).thenReturn(Optional.of(user));

        UserResponse result = userService.findUserById(id);

        assertThat(result.id()).isEqualTo(id);
        assertThat(result.username()).isEqualTo("testuser");
        assertThat(result.enabled()).isTrue();
        assertThat(result.roles()).isEmpty();
    }

    @Test
    void findUserById_NotFound() {
        UUID id = UUID.randomUUID();
        when(userRepository.findUserById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findUserById(id))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("User not found");
    }

    @Test
    void getAllUsers_Success() {
        UUID id = UUID.randomUUID();
        UserProjection user = mock(UserProjection.class);
        when(user.getId()).thenReturn(id);
        when(user.getUsername()).thenReturn("testuser");
        when(user.getEnabled()).thenReturn(true);
        when(user.getRoles()).thenReturn(Set.of());

        Page<UserProjection> userPage = new PageImpl<>(List.of(user));
        when(userRepository.findAllUsers(any(PageRequest.class))).thenReturn(userPage);

        Page<UserResponse> result = userService.getAllUsers(0, 10);

        assertThat(result.getContent()).hasSize(1);
        UserResponse response = result.getContent().get(0);
        assertThat(response.id()).isEqualTo(id);
        assertThat(response.username()).isEqualTo("testuser");
        assertThat(response.enabled()).isTrue();
        assertThat(response.roles()).isEmpty();
    }

    @Test
    void deleteUser_Success() {
        UUID id = UUID.randomUUID();
        when(userRepository.existsById(id)).thenReturn(true);

        userService.deleteUser(id);

        verify(userRepository).deleteById(id);
    }

    @Test
    void deleteUser_NotFound() {
        UUID id = UUID.randomUUID();
        when(userRepository.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> userService.deleteUser(id))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("User not found");

        verify(userRepository, never()).deleteById(any());
    }
} 