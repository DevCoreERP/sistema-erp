package com.devcoreerp.backend_erp.subcripcion.infrastructure.controllers;

import com.devcoreerp.backend_erp.auth.infrastructure.config.ApiConfig;
import com.devcoreerp.backend_erp.subcripcion.application.services.SuscripcionService;
import com.devcoreerp.backend_erp.subcripcion.infrastructure.dtos.PlanAccessResponseDTO;
import com.devcoreerp.backend_erp.subcripcion.infrastructure.dtos.SuscripcionCompraRequestDTO;
import com.devcoreerp.backend_erp.subcripcion.infrastructure.dtos.SuscripcionResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiConfig.API_BASE_PATH + "/suscripciones")
@Tag(name = "Suscripciones", description = "Consulta y contratacion de planes del tenant actual")
public class SuscripcionController {

    private final SuscripcionService suscripcionService;

    public SuscripcionController(SuscripcionService suscripcionService) {
        this.suscripcionService = suscripcionService;
    }

    @GetMapping("/actual")
    @PreAuthorize("hasAuthority('SUSCRIPCION_VER')")
    @Operation(
            summary = "SUSCRIPCION_VER",
            description = "Consulta la suscripcion actual o la ultima suscripcion del tenant autenticado. Requiere permiso SUSCRIPCION_VER y muestra si esta vigente, vencida, en prueba o pagada.")
    public ResponseEntity<SuscripcionResponseDTO> obtenerActual() {
        return ResponseEntity.ok(suscripcionService.obtenerActual());
    }

    @GetMapping("/actual/accesos")
    @PreAuthorize("hasAuthority('SUSCRIPCION_VER')")
    @Operation(
            summary = "SUSCRIPCION_ACCESOS_VER",
            description = "Consulta el plan vigente del tenant y los permisos habilitados por sus modulos. Sirve para que el frontend sepa que funcionalidades debe mostrar segun el plan activo.")
    public ResponseEntity<PlanAccessResponseDTO> obtenerAccesosActuales() {
        return ResponseEntity.ok(suscripcionService.obtenerAccesosActuales());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SUSCRIPCION_GESTIONAR')")
    @Operation(
            summary = "SUSCRIPCION_GESTIONAR",
            description = "Adquiere un plan pagado para el tenant autenticado. Requiere permiso SUSCRIPCION_GESTIONAR. Bloquea la compra si existe una prueba o suscripcion activa vigente, crea suscripcion ACTIVA y registra un pago simulado PAGADO.")
    public ResponseEntity<SuscripcionResponseDTO> adquirirPlan(@RequestBody @Valid SuscripcionCompraRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(suscripcionService.adquirirPlan(dto));
    }
}
