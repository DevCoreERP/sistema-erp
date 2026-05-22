package com.devcoreerp.backend_erp.auth.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

class UsuarioAuthoritiesTest {

    @Test
    void usuarioConRolActivoYPermisoActivoObtieneAuthority() {
        Permission permission = Permission.builder()
            .id(1L)
            .code("USUARIO_CREAR")
            .description("Crear usuarios")
            .estado(true)
            .build();
        Role role = Role.builder()
            .id(1L)
            .name("Administrador RRHH")
            .description("Administra RRHH")
            .tipo(RoleType.CUSTOM)
            .estado(true)
            .permissions(Set.of(permission))
            .build();
        Usuario usuario = Usuario.builder()
            .username("admin")
            .email("admin@example.com")
            .password("secret")
            .firstName("Admin")
            .surnames("RRHH")
            .estado(true)
            .enabled(true)
            .roles(Set.of(role))
            .build();

        assertThat(usuario.getAuthorities())
            .extracting(GrantedAuthority::getAuthority)
            .containsExactly("USUARIO_CREAR");
    }

    @Test
    void rolesOPermisosInactivosNoGeneranAuthorities() {
        Permission activePermission = Permission.builder()
            .id(1L)
            .code("USUARIO_CREAR")
            .description("Crear usuarios")
            .estado(true)
            .build();
        Permission inactivePermission = Permission.builder()
            .id(2L)
            .code("USUARIO_ELIMINAR")
            .description("Eliminar usuarios")
            .estado(false)
            .build();
        Role inactiveRole = Role.builder()
            .id(1L)
            .name("Rol inactivo")
            .description("No debe autorizar")
            .tipo(RoleType.CUSTOM)
            .estado(false)
            .permissions(Set.of(activePermission))
            .build();
        Role activeRole = Role.builder()
            .id(2L)
            .name("Rol activo")
            .description("Debe filtrar permisos")
            .tipo(RoleType.CUSTOM)
            .estado(true)
            .permissions(Set.of(inactivePermission))
            .build();
        Usuario usuario = Usuario.builder()
            .username("user")
            .email("user@example.com")
            .password("secret")
            .firstName("User")
            .surnames("Demo")
            .estado(true)
            .enabled(true)
            .roles(Set.of(inactiveRole, activeRole))
            .build();

        assertThat(usuario.getAuthorities()).isEmpty();
    }
}
