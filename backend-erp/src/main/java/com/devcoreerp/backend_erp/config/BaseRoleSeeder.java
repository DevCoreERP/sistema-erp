package com.devcoreerp.backend_erp.config;

import com.devcoreerp.backend_erp.auth.domain.Permission;
import com.devcoreerp.backend_erp.auth.domain.Role;
import com.devcoreerp.backend_erp.auth.domain.RoleType;
import com.devcoreerp.backend_erp.auth.infrastructure.persistance.PermissionRepository;
import com.devcoreerp.backend_erp.auth.infrastructure.persistance.RoleRepository;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Inicializa roles base dentro del schema tenant activo.
 * No corre automaticamente al iniciar la aplicacion.
 * Debe ejecutarse con TenantContext ya configurado.
 */
@Service
public class BaseRoleSeeder {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public BaseRoleSeeder(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    @Transactional
    public void initializeBaseRolesForCurrentTenant() {
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
                        "PERMISO_LISTAR",
                        "TURNO_CREAR",
                        "TURNO_LISTAR",
                        "TURNO_EDITAR",
                        "TURNO_ACTUALIZAR",
                        "TURNO_ELIMINAR",
                        "ASIGNACION_TURNO_CREAR",
                        "ASIGNACION_TURNO_LISTAR",
                        "ASIGNACION_TURNO_EDITAR",
                        "ASIGNACION_TURNO_ELIMINAR",
                        "AGENDA_TURNO_LISTAR",
                        "DEPARTAMENTO_CREAR",
                        "DEPARTAMENTO_LISTAR",
                        "DEPARTAMENTO_EDITAR",
                        "DEPARTAMENTO_ELIMINAR",
                        "SALDO_CREAR",
                        "SALDO_LISTAR",
                        "SALDO_EDITAR",
                        "SALDO_ELIMINAR",
                        "SOLICITUD_CREAR",
                        "SOLICITUD_LISTAR",
                        "SOLICITUD_EDITAR",
                        "SOLICITUD_ELIMINAR",
                        "SOLICITUD_DELETE",
                        "SUSCRIPCION_VER",
                        "SUSCRIPCION_GESTIONAR",
                        "METODO_PAGO_GESTIONAR",
                        "PAGO_LISTAR"));

        // 2. DIRECTOR: Gestión estratégica de RRHH y Departamentos.
        createOrUpdateRole(
                "DIRECTOR",
                "Director de Departamento - Gestión estratégica y financiera de RRHH",
                List.of(
                        "USUARIO_LISTAR", // Para auditar personal de su área
                        "DEPARTAMENTO_CREAR", "DEPARTAMENTO_LISTAR", "DEPARTAMENTO_EDITAR", "DEPARTAMENTO_ELIMINAR",
                        "SALDO_CREAR", "SALDO_LISTAR", "SALDO_EDITAR", "SALDO_ELIMINAR",
                        "SOLICITUD_LISTAR", "SOLICITUD_EDITAR", "SOLICITUD_ELIMINAR", "SOLICITUD_DELETE",
                        "TURNO_LISTAR", "AGENDA_TURNO_LISTAR"));

        // 3. SUPERVISOR: Control operativo de turnos y cuadrantes del equipo.
        createOrUpdateRole(
                "SUPERVISOR",
                "Supervisor de Equipo - Gestión de turnos y validación operativa",
                List.of(
                        "USUARIO_LISTAR", // Para ver los miembros de su equipo
                        "TURNO_CREAR", "TURNO_LISTAR", "TURNO_EDITAR", "TURNO_ACTUALIZAR", "TURNO_ELIMINAR",
                        "ASIGNACION_TURNO_CREAR", "ASIGNACION_TURNO_LISTAR", "ASIGNACION_TURNO_EDITAR",
                        "ASIGNACION_TURNO_ELIMINAR",
                        "AGENDA_TURNO_LISTAR",
                        "SALDO_LISTAR", // Para revisar si su equipo tiene días disponibles antes de asignar turnos
                        "SOLICITUD_LISTAR", "SOLICITUD_EDITAR" // Para revisar/pre-aprobar bajas o vacaciones
                ));

        // 4. EMPLEADO: Autoservicio (Self-service) del trabajador.
        createOrUpdateRole(
                "EMPLEADO",
                "Trabajador - Consulta de turnos y gestión de solicitudes personales",
                List.of(
                        "AGENDA_TURNO_LISTAR", // Ver su propio calendario
                        "SALDO_LISTAR", // Ver cuántos días de vacaciones le quedan
                        "SOLICITUD_CREAR", // Solicitar vacaciones, permisos, etc.
                        "SOLICITUD_LISTAR" // Ver si su supervisor o director le aprobaron el pedido
                ));
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
