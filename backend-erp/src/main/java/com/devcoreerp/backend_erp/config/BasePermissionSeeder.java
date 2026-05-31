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
 * si ya esta inserto lo ignora. Los permisos viven en public.permissions.
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
        seedBasePermissions();
    }

    @Transactional
    public void seedBasePermissions() {
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
            new PermissionSeed("TURNO_ACTUALIZAR", "Actualizar turnos"),
            new PermissionSeed("TURNO_ELIMINAR", "Eliminar o desactivar usuarios"),

            new PermissionSeed("ASIGNACION_TURNO_CREAR", "Crear asignaciones de turno laboral"),
            new PermissionSeed("ASIGNACION_TURNO_LISTAR", "Listar o consultar asignaciones de turno laboral"),
            new PermissionSeed("ASIGNACION_TURNO_EDITAR", "Editar o finalizar asignaciones de turno laboral"),
            new PermissionSeed("ASIGNACION_TURNO_ELIMINAR", "Desactivar asignaciones de turno laboral"),

            new PermissionSeed("AGENDA_TURNO_LISTAR", "Consultar agenda propia de turnos laborales"),

            new PermissionSeed("DEPARTAMENTO_CREAR", "Crear departamentos"),
            new PermissionSeed("DEPARTAMENTO_LISTAR", "Listar departamentos"),
            new PermissionSeed("DEPARTAMENTO_EDITAR", "Editar departamentos"),
            new PermissionSeed("DEPARTAMENTO_ELIMINAR", "Eliminar departamentos"),

            new PermissionSeed("SALDO_CREAR", "Crear saldos"),
            new PermissionSeed("SALDO_LISTAR", "Listar saldos"),
            new PermissionSeed("SALDO_EDITAR", "Editar saldos"),
            new PermissionSeed("SALDO_ELIMINAR", "Eliminar saldos"),

            new PermissionSeed("SOLICITUD_CREAR", "Crear solicitud"),
            new PermissionSeed("SOLICITUD_LISTAR", "Listar solicitud"),
            new PermissionSeed("SOLICITUD_EDITAR", "Editar solicitud"),
            new PermissionSeed("SOLICITUD_ELIMINAR", "Eliminar solicitud"),
            new PermissionSeed("SOLICITUD_DELETE", "Eliminar solicitud")
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
