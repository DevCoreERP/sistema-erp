package com.devcoreerp.backend_erp.config;

import com.devcoreerp.backend_erp.auth.domain.Permission;
import com.devcoreerp.backend_erp.auth.infrastructure.persistance.PermissionRepository;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.core.annotation.Order;

/**
 * Clase que crea los permisos base para el sistema se ejecuta cuando corre
 * si ya esta inserto lo ignora debe estar el propertis en update
 */
@Component
@Order(1)
public class BasePermissionSeeder implements CommandLineRunner {

    private final PermissionRepository permissionRepository;

    public BasePermissionSeeder(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        List<PermissionSeed> permissions = List.of(
            new PermissionSeed("USUARIO_CREAR", "Crear usuarios"),
            new PermissionSeed("USUARIO_LISTAR", "Listar o consultar usuarios"),
            new PermissionSeed("USUARIO_EDITAR", "Editar usuarios"),
            new PermissionSeed("USUARIO_ELIMINAR", "Eliminar o desactivar usuarios"),
            new PermissionSeed("USUARIO_ASIGNAR_ROL", "Asignar roles a usuarios"),

            new PermissionSeed("ROL_CREAR", "Crear roles"),
            new PermissionSeed("ROL_LISTAR", "Listar o consultar roles"),
            new PermissionSeed("ROL_EDITAR", "Editar roles"),
            new PermissionSeed("ROL_ELIMINAR", "Eliminar o desactivar roles"),
            new PermissionSeed("ROL_ASIGNAR_PERMISO", "Asignar permisos a roles"),

            new PermissionSeed("PERMISO_LISTAR", "Listar o consultar permisos"),

            new PermissionSeed("TURNO_CREAR", "Crear usuarios"),
            new PermissionSeed("TURNO_LISTAR", "Listar o consultar usuarios"),
            new PermissionSeed("TURNO_EDITAR", "Editar usuarios"),
            new PermissionSeed("TURNO_ELIMINAR", "Eliminar o desactivar usuarios"),

            new PermissionSeed("ASIGNACION_TURNO_CREAR", "Crear asignaciones de turno laboral"),
            new PermissionSeed("ASIGNACION_TURNO_LISTAR", "Listar o consultar asignaciones de turno laboral"),
            new PermissionSeed("ASIGNACION_TURNO_EDITAR", "Editar o finalizar asignaciones de turno laboral"),
            new PermissionSeed("ASIGNACION_TURNO_ELIMINAR", "Desactivar asignaciones de turno laboral"),

            new PermissionSeed("AGENDA_TURNO_LISTAR", "Consultar agenda propia de turnos laborales")
        );

        permissions.forEach(seed -> {
            if (!permissionRepository.existsByCode(seed.code())) {
                permissionRepository.save(new Permission(seed.code(), seed.description()));
            }
        });
    }

    private record PermissionSeed(String code, String description) {
    }
}
