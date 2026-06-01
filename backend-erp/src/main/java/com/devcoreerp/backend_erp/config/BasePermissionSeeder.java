package com.devcoreerp.backend_erp.config;

import com.devcoreerp.backend_erp.auth.domain.Permission;
import com.devcoreerp.backend_erp.auth.infrastructure.persistance.PermissionRepository;
import com.devcoreerp.backend_erp.plan.domain.Modulo;
import com.devcoreerp.backend_erp.plan.infrastructure.persistance.ModuloRepository;
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
    private final ModuloRepository moduloRepository;

    public BasePermissionSeeder(PermissionRepository permissionRepository, ModuloRepository moduloRepository) {
        this.permissionRepository = permissionRepository;
        this.moduloRepository = moduloRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        seedBasePermissions();
    }

    @Transactional
    public void seedBasePermissions() {
        List<PermissionSeed> permissions = List.of(
            new PermissionSeed("USUARIO_CREAR", "Crear usuarios", "Core HR"),
            new PermissionSeed("USUARIO_LISTAR", "Listar o consultar usuarios", "Core HR"),
            new PermissionSeed("USUARIO_EDITAR", "Editar usuarios", "Core HR"),
            new PermissionSeed("USUARIO_ELIMINAR", "Eliminar o desactivar usuarios", "Core HR"),
            new PermissionSeed("USUARIO_ASIGNAR_ROL", "Asignar roles a usuarios", "Core HR"),

            new PermissionSeed("ROL_CREAR", "Crear roles", "Core HR"),
            new PermissionSeed("ROL_LISTAR", "Listar o consultar roles", "Core HR"),
            new PermissionSeed("ROL_EDITAR", "Editar roles", "Core HR"),
            new PermissionSeed("ROL_ELIMINAR", "Eliminar o desactivar roles", "Core HR"),
            new PermissionSeed("ROL_ASIGNAR_PERMISO", "Asignar permisos a roles", "Core HR"),

            new PermissionSeed("PERMISO_LISTAR", "Listar o consultar permisos", "Core HR"),

            new PermissionSeed("TURNO_CREAR", "Crear turnos", "Core HR"),
            new PermissionSeed("TURNO_LISTAR", "Listar o consultar turnos", "Core HR"),
            new PermissionSeed("TURNO_EDITAR", "Editar turnos", "Core HR"),
            new PermissionSeed("TURNO_ACTUALIZAR", "Actualizar turnos", "Core HR"),
            new PermissionSeed("TURNO_ELIMINAR", "Eliminar o desactivar turnos", "Core HR"),

            new PermissionSeed("ASIGNACION_TURNO_CREAR", "Crear asignaciones de turno laboral", "Core HR"),
            new PermissionSeed("ASIGNACION_TURNO_LISTAR", "Listar o consultar asignaciones de turno laboral", "Core HR"),
            new PermissionSeed("ASIGNACION_TURNO_EDITAR", "Editar o finalizar asignaciones de turno laboral", "Core HR"),
            new PermissionSeed("ASIGNACION_TURNO_ELIMINAR", "Desactivar asignaciones de turno laboral", "Core HR"),

            new PermissionSeed("AGENDA_TURNO_LISTAR", "Consultar agenda propia de turnos laborales", "Autoservicio del empleado"),

            new PermissionSeed("DEPARTAMENTO_CREAR", "Crear departamentos", "Core HR"),
            new PermissionSeed("DEPARTAMENTO_LISTAR", "Listar departamentos", "Core HR"),
            new PermissionSeed("DEPARTAMENTO_EDITAR", "Editar departamentos", "Core HR"),
            new PermissionSeed("DEPARTAMENTO_ELIMINAR", "Eliminar departamentos", "Core HR"),

            new PermissionSeed("SALDO_CREAR", "Crear saldos", "Ausencias"),
            new PermissionSeed("SALDO_LISTAR", "Listar saldos", "Ausencias"),
            new PermissionSeed("SALDO_EDITAR", "Editar saldos", "Ausencias"),
            new PermissionSeed("SALDO_ELIMINAR", "Eliminar saldos", "Ausencias"),

            new PermissionSeed("SOLICITUD_CREAR", "Crear solicitud", "Ausencias"),
            new PermissionSeed("SOLICITUD_LISTAR", "Listar solicitud", "Ausencias"),
            new PermissionSeed("SOLICITUD_EDITAR", "Editar solicitud", "Ausencias"),
            new PermissionSeed("SOLICITUD_ELIMINAR", "Eliminar solicitud", "Ausencias"),
            new PermissionSeed("SOLICITUD_DELETE", "Eliminar solicitud", "Ausencias"),

            new PermissionSeed("SUSCRIPCION_VER", "Consultar suscripcion del tenant", "Facturacion SaaS"),
            new PermissionSeed("SUSCRIPCION_GESTIONAR", "Gestionar suscripcion del tenant", "Facturacion SaaS"),
            new PermissionSeed("METODO_PAGO_GESTIONAR", "Gestionar metodos de pago del tenant", "Facturacion SaaS"),
            new PermissionSeed("PAGO_LISTAR", "Listar historial de pagos del tenant", "Facturacion SaaS")
        );

        permissions.forEach(seed -> {
            Modulo modulo = moduloRepository.findByNombre(seed.moduloNombre())
                    .orElseThrow(() -> new IllegalStateException("Modulo no encontrado: " + seed.moduloNombre()));

            Permission permission = permissionRepository.findByCode(seed.code())
                    .orElseGet(() -> new Permission(seed.code(), seed.description()));

            permission.setDescription(seed.description());
            permission.setEstado(true);
            permission.setModulo(modulo);
            permissionRepository.save(permission);
        });
    }

    private record PermissionSeed(String code, String description, String moduloNombre) {
    }
}
