package com.devcoreerp.backend_erp.control_asistencia.infrastructure.controller;

import com.devcoreerp.backend_erp.auth.infrastructure.config.ApiConfig;
import com.devcoreerp.backend_erp.control_asistencia.domain.services.AsignacionTurnoService;
import com.devcoreerp.backend_erp.control_asistencia.infrastructure.dto.AsignacionTurnoRequestDTO;
import com.devcoreerp.backend_erp.control_asistencia.infrastructure.dto.AsignacionTurnoResponseDTO;
import com.devcoreerp.backend_erp.control_asistencia.infrastructure.dto.AsignacionTurnoUpdateDTO;
import com.devcoreerp.backend_erp.control_asistencia.infrastructure.dto.CambiarEstadoAsignacionTurnoDTO;
import com.devcoreerp.backend_erp.control_asistencia.infrastructure.dto.FinalizarAsignacionTurnoDTO;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiConfig.API_BASE_PATH + "/control-asistencia/asignaciones-turno")
public class AsignacionTurnoController {

    private final AsignacionTurnoService asignacionTurnoService;

    public AsignacionTurnoController(AsignacionTurnoService asignacionTurnoService) {
        this.asignacionTurnoService = asignacionTurnoService;
    }

    @Operation(
            summary = "Crear asignacion de turno laboral",
            description = "Registra un turno laboral para un empleado validando usuario, turno activo, fechas y solapamientos. Requiere permiso ASIGNACION_TURNO_CREAR.")
    @PostMapping
    @PreAuthorize("hasAuthority('ASIGNACION_TURNO_CREAR')")
    public ResponseEntity<AsignacionTurnoResponseDTO> crearAsignacion(
            @RequestBody @Valid AsignacionTurnoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(asignacionTurnoService.crearAsignacion(dto));
    }

    @Operation(
            summary = "Listar asignaciones de turno",
            description = "Obtiene todas las asignaciones de turno registradas para consulta administrativa e historica. Requiere permiso ASIGNACION_TURNO_LISTAR.")
    @GetMapping
    @PreAuthorize("hasAuthority('ASIGNACION_TURNO_LISTAR')")
    public ResponseEntity<List<AsignacionTurnoResponseDTO>> obtenerTodos() {
        return ResponseEntity.ok(asignacionTurnoService.obtenerTodos());
    }

    @Operation(
            summary = "Consultar asignacion de turno por ID",
            description = "Obtiene el detalle de una asignacion de turno especifica por su identificador. Requiere permiso ASIGNACION_TURNO_LISTAR.")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ASIGNACION_TURNO_LISTAR')")
    public ResponseEntity<AsignacionTurnoResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(asignacionTurnoService.obtenerPorId(id));
    }

    @Operation(
            summary = "Consultar asignaciones por usuario",
            description = "Lista el historial de turnos asignados a un empleado segun su identificador de usuario. Requiere permiso ASIGNACION_TURNO_LISTAR.")
    @GetMapping("/usuario/{usuarioId}")
    @PreAuthorize("hasAuthority('ASIGNACION_TURNO_LISTAR')")
    public ResponseEntity<List<AsignacionTurnoResponseDTO>> obtenerPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(asignacionTurnoService.obtenerPorUsuario(usuarioId));
    }

    @Operation(
            summary = "Consultar asignaciones por turno",
            description = "Lista las asignaciones laborales asociadas a un turno especifico. Requiere permiso ASIGNACION_TURNO_LISTAR.")
    @GetMapping("/turno/{turnoId}")
    @PreAuthorize("hasAuthority('ASIGNACION_TURNO_LISTAR')")
    public ResponseEntity<List<AsignacionTurnoResponseDTO>> obtenerPorTurno(@PathVariable Long turnoId) {
        return ResponseEntity.ok(asignacionTurnoService.obtenerPorTurno(turnoId));
    }

    @Operation(
            summary = "Listar asignaciones activas",
            description = "Obtiene las asignaciones de turno actualmente activas para seguimiento de planificacion laboral. Requiere permiso ASIGNACION_TURNO_LISTAR.")
    @GetMapping("/activas")
    @PreAuthorize("hasAuthority('ASIGNACION_TURNO_LISTAR')")
    public ResponseEntity<List<AsignacionTurnoResponseDTO>> obtenerActivas() {
        return ResponseEntity.ok(asignacionTurnoService.obtenerActivas());
    }

    @Operation(
            summary = "Consultar turno vigente por usuario",
            description = "Obtiene la asignacion activa que corresponde al empleado en la fecha actual. Requiere permiso ASIGNACION_TURNO_LISTAR.")
    @GetMapping("/usuario/{usuarioId}/vigente")
    @PreAuthorize("hasAuthority('ASIGNACION_TURNO_LISTAR')")
    public ResponseEntity<AsignacionTurnoResponseDTO> obtenerVigentePorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(asignacionTurnoService.obtenerVigentePorUsuario(usuarioId));
    }

    @Operation(
            summary = "Actualizar asignacion de turno",
            description = "Modifica los datos de una asignacion laboral existente validando fechas, turno activo y ausencia de solapamientos. Requiere permiso ASIGNACION_TURNO_EDITAR.")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ASIGNACION_TURNO_EDITAR')")
    public ResponseEntity<AsignacionTurnoResponseDTO> actualizarAsignacion(
            @PathVariable Long id,
            @RequestBody @Valid AsignacionTurnoUpdateDTO dto) {
        return ResponseEntity.ok(asignacionTurnoService.actualizarAsignacion(id, dto));
    }

    @Operation(
            summary = "Cambiar estado de asignacion de turno",
            description = "Activa o desactiva logicamente una asignacion de turno sin eliminar su historial. Requiere permiso ASIGNACION_TURNO_ELIMINAR.")
    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAuthority('ASIGNACION_TURNO_ELIMINAR')")
    public ResponseEntity<AsignacionTurnoResponseDTO> cambiarEstado(
            @PathVariable Long id,
            @RequestBody @Valid CambiarEstadoAsignacionTurnoDTO dto) {
        return ResponseEntity.ok(asignacionTurnoService.cambiarEstadoAsignacion(id, dto.getEstado()));
    }

    @Operation(
            summary = "Finalizar asignacion de turno",
            description = "Registra la fecha de finalizacion de una asignacion laboral y la desactiva conservando el historial. Requiere permiso ASIGNACION_TURNO_EDITAR.")
    @PatchMapping("/{id}/finalizar")
    @PreAuthorize("hasAuthority('ASIGNACION_TURNO_EDITAR')")
    public ResponseEntity<AsignacionTurnoResponseDTO> finalizarAsignacion(
            @PathVariable Long id,
            @RequestBody @Valid FinalizarAsignacionTurnoDTO dto) {
        return ResponseEntity.ok(asignacionTurnoService.finalizarAsignacion(id, dto.getFechaFin()));
    }
}
