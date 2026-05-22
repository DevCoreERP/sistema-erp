package com.devcoreerp.backend_erp.auth.application.services;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.devcoreerp.backend_erp.auth.domain.Role;
import com.devcoreerp.backend_erp.auth.domain.RoleType;
import com.devcoreerp.backend_erp.auth.domain.Usuario;
import com.devcoreerp.backend_erp.auth.domain.services.TokenService;
import com.devcoreerp.backend_erp.auth.infrastructure.persistance.RoleRepository;
import com.devcoreerp.backend_erp.auth.infrastructure.persistance.UsuarioRepository;
import java.util.HashSet;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private TokenService tokenService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationConfiguration authenticationConfiguration;

    @Test
    void usuarioInactivoNoPuedeCargarseParaAutenticacion() {
        AuthServiceImpl service = newService();
        Usuario usuario = Usuario.builder()
            .id(1L)
            .username("demo")
            .email("demo@example.com")
            .password("secret")
            .firstName("Demo")
            .surnames("User")
            .estado(false)
            .enabled(true)
            .roles(new HashSet<>())
            .build();

        when(usuarioRepository.findByEmail("demo@example.com")).thenReturn(Optional.of(usuario));

        assertThatThrownBy(() -> service.loadUserByUsername("demo@example.com"))
            .isInstanceOf(DisabledException.class);
    }

    @Test
    void noDuplicaAsignacionUsuarioRol() {
        AuthServiceImpl service = newService();
        Role role = Role.builder()
            .id(10L)
            .name("Reclutador")
            .description("Gestiona seleccion")
            .tipo(RoleType.CUSTOM)
            .estado(true)
            .permissions(new HashSet<>())
            .build();
        Usuario usuario = Usuario.builder()
            .id(1L)
            .username("demo")
            .email("demo@example.com")
            .password("secret")
            .firstName("Demo")
            .surnames("User")
            .estado(true)
            .enabled(true)
            .roles(new HashSet<>())
            .build();
        usuario.getRoles().add(role);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(roleRepository.findById(10L)).thenReturn(Optional.of(role));

        assertThatThrownBy(() -> service.assignRoleToUser(1L, 10L))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("El rol ya esta asignado");
    }

    private AuthServiceImpl newService() {
        return new AuthServiceImpl(
            usuarioRepository,
            roleRepository,
            tokenService,
            passwordEncoder,
            authenticationConfiguration
        );
    }
}
