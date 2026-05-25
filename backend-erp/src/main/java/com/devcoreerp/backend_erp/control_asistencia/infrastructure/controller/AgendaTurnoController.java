package com.devcoreerp.backend_erp.control_asistencia.infrastructure.controller;

import com.devcoreerp.backend_erp.auth.domain.Usuario;
import com.devcoreerp.backend_erp.auth.infrastructure.config.ApiConfig;
import com.devcoreerp.backend_erp.control_asistencia.domain.services.AgendaTurnoService;
import com.devcoreerp.backend_erp.control_asistencia.infrastructure.dto.AgendaTurnoResponseDTO;

import io.swagger.v3.oas.annotations.Operation;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiConfig.API_BASE_PATH + "/control-asistencia/agenda-turnos")
public class AgendaTurnoController {

    private final AgendaTurnoService agendaTurnoService;

    public AgendaTurnoController(AgendaTurnoService agendaTurnoService) {
        this.agendaTurnoService = agendaTurnoService;
    }

    @Operation(
            summary = "Consultar mis turnos proximos",
            description = "Muestra la agenda de turnos del empleado autenticado para los proximos dias. El parametro opcional dias define el rango de consulta entre 1 y 31 dias; por defecto consulta 7 dias. Requiere permiso AGENDA_TURNO_LISTAR.")
    @GetMapping("/mis-turnos")
    @PreAuthorize("hasAuthority('AGENDA_TURNO_LISTAR')")
    public ResponseEntity<List<AgendaTurnoResponseDTO>> consultarMisTurnos(
            @AuthenticationPrincipal Usuario usuario,
            @RequestParam(required = false) Integer dias) {
        return ResponseEntity.ok(agendaTurnoService.consultarMiAgenda(usuario.getId(), dias));
    }
}
