package com.devcoreerp.backend_erp.control_asistencia.infrastructure.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.devcoreerp.backend_erp.auth.infrastructure.config.ApiConfig;
import com.devcoreerp.backend_erp.control_asistencia.domain.services.TurnoService;
import com.devcoreerp.backend_erp.control_asistencia.infrastructure.dto.TurnoRequestDTO;
import com.devcoreerp.backend_erp.control_asistencia.infrastructure.dto.TurnoResponseDTO;

import io.swagger.v3.oas.annotations.Operation;

import java.util.List;

@RestController
@RequestMapping(ApiConfig.API_BASE_PATH + "/turno")
@RequiredArgsConstructor
public class TurnoController {

    private final TurnoService turnoService;

    @Operation(
            summary = "Registrar turno laboral",
            description = "Crea un turno laboral con nombre, hora de inicio, hora de fin y descripcion. Requiere permiso TURNO_CREAR.")
    @PostMapping
    @PreAuthorize("hasAuthority('TURNO_CREAR')")
    public ResponseEntity<TurnoResponseDTO> registrarTurno(@RequestBody TurnoRequestDTO dto) {
        return new ResponseEntity<>(turnoService.crearTurno(dto), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Actualizar turno laboral",
            description = "Modifica los datos principales de un turno laboral existente. Requiere permiso TURNO_ACTUALIZAR.")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('TURNO_ACTUALIZAR')")
    public ResponseEntity<TurnoResponseDTO> modificarTurno(@PathVariable Long id, @RequestBody TurnoRequestDTO dto) {
        return ResponseEntity.ok(turnoService.actualizarTurno(id, dto));
    }

    @Operation(
            summary = "Consultar turno laboral por ID",
            description = "Obtiene el detalle de un turno laboral especifico por su identificador. Requiere permiso TURNO_LISTAR.")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('TURNO_LISTAR')")
    public ResponseEntity<TurnoResponseDTO> obtenerTurnoPorId(@PathVariable Long id) {
        return ResponseEntity.ok(turnoService.obtenerPorId(id));
    }

    @Operation(
            summary = "Listar turnos laborales",
            description = "Obtiene el listado de turnos laborales registrados en el sistema. Requiere permiso TURNO_LISTAR.")
    @GetMapping
    @PreAuthorize("hasAuthority('TURNO_LISTAR')")
    public ResponseEntity<List<TurnoResponseDTO>> obtenerTodosLosTurnos() {
        return ResponseEntity.ok(turnoService.obtenerTodos());
    }

    @Operation(
            summary = "Activar o desactivar turno laboral",
            description = "Cambia el estado de un turno laboral sin eliminarlo fisicamente, conservando su historial de uso. Requiere permiso TURNO_ELIMINAR.")
    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAuthority('TURNO_ELIMINAR')")
    public ResponseEntity<TurnoResponseDTO> cambiarEstado(
            @PathVariable Long id,
            @RequestParam Boolean activar) {
        return ResponseEntity.ok(turnoService.cambiarEstadoTurno(id, activar));
    }
}
