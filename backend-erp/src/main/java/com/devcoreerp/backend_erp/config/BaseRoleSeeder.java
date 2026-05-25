package com.devcoreerp.backend_erp.config;

import com.devcoreerp.backend_erp.auth.domain.Permission;
import com.devcoreerp.backend_erp.auth.domain.Role;
import com.devcoreerp.backend_erp.auth.domain.RoleType;
import com.devcoreerp.backend_erp.auth.infrastructure.persistance.PermissionRepository;
import com.devcoreerp.backend_erp.auth.infrastructure.persistance.RoleRepository;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.core.annotation.Order;

/**
 * Clase que crea los roles base del sistema y les asigna sus permisos.
 * Se ejecuta cada vez que levanta la aplicación.
 * Si el rol ya existe, actualiza sus permisos base.
 */
@Component
@Order(2)
public class BaseRoleSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public BaseRoleSeeder(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        createOrUpdateRole(
            "ADMIN",
            "Administrador del sistema con acceso completo",
            List.of(
                "USUARIO_CREAR",
                "USUARIO_LISTAR",
                "USUARIO_EDITAR",
                "USUARIO_ELIMINAR",
                "USUARIO_ASIGNAR_ROL",
                "ROL_CREAR",
                "ROL_LISTAR",
                "ROL_EDITAR",
                "ROL_ELIMINAR",
                "ROL_ASIGNAR_PERMISO",
                "PERMISO_LISTAR"
            )
        );

        createOrUpdateRole(
            "SUPERVISOR",
            "Supervisor del sistema con acceso de consulta y gestión limitada",
            List.of(
                "USUARIO_LISTAR",
                "ROL_LISTAR",
                "PERMISO_LISTAR"
            )
        );
    }

    private void createOrUpdateRole(String name, String description, List<String> permissionCodes) {
        Set<Permission> permissions = permissionCodes.stream()
            .map(code -> permissionRepository.findByCode(code)
                .orElseThrow(() -> new IllegalStateException("Permiso no encontrado: " + code)))
            .filter(permission -> Boolean.TRUE.equals(permission.getEstado()))
            .collect(Collectors.toSet());

        Role role = roleRepository.findByName(name)
            .orElseGet(() -> new Role(name, description, RoleType.SYSTEM));

        role.setDescription(description);
        role.setTipo(RoleType.SYSTEM);
        role.setEstado(true);
        role.setPermissions(permissions);

        roleRepository.save(role);
    }
}