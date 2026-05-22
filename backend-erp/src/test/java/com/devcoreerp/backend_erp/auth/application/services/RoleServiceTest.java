package com.devcoreerp.backend_erp.auth.application.services;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.devcoreerp.backend_erp.auth.domain.Permission;
import com.devcoreerp.backend_erp.auth.domain.Role;
import com.devcoreerp.backend_erp.auth.domain.RoleType;
import com.devcoreerp.backend_erp.auth.infrastructure.persistance.PermissionRepository;
import com.devcoreerp.backend_erp.auth.infrastructure.persistance.RoleRepository;
import java.util.HashSet;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PermissionRepository permissionRepository;

    @Test
    void noAsignaPermisoInexistenteARol() {
        RoleService service = new RoleService(roleRepository, permissionRepository);
        Role role = activeRole();

        when(roleRepository.findWithPermissionsById(1L)).thenReturn(Optional.of(role));
        when(permissionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.assignPermissionToRole(1L, 99L))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("Permiso no encontrado");
    }

    @Test
    void noDuplicaAsignacionRolPermiso() {
        RoleService service = new RoleService(roleRepository, permissionRepository);
        Role role = activeRole();
        Permission permission = Permission.builder()
            .id(2L)
            .code("USUARIO_CREAR")
            .description("Crear usuarios")
            .estado(true)
            .build();
        role.getPermissions().add(permission);

        when(roleRepository.findWithPermissionsById(1L)).thenReturn(Optional.of(role));
        when(permissionRepository.findById(2L)).thenReturn(Optional.of(permission));

        assertThatThrownBy(() -> service.assignPermissionToRole(1L, 2L))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("El permiso ya esta asignado");
    }

    private Role activeRole() {
        return Role.builder()
            .id(1L)
            .name("Administrador RRHH")
            .description("Administra RRHH")
            .tipo(RoleType.CUSTOM)
            .estado(true)
            .permissions(new HashSet<>())
            .build();
    }
}
