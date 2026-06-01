package com.devcoreerp.backend_erp.subcripcion.infrastructure.controllers;

import com.devcoreerp.backend_erp.auth.infrastructure.config.ApiConfig;
import com.devcoreerp.backend_erp.subcripcion.application.services.SuscripcionService;
import com.devcoreerp.backend_erp.subcripcion.infrastructure.dtos.PlanAccessResponseDTO;
import com.devcoreerp.backend_erp.subcripcion.infrastructure.dtos.SuscripcionCompraRequestDTO;
import com.devcoreerp.backend_erp.subcripcion.infrastructure.dtos.SuscripcionResponseDTO;
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
public class SuscripcionController {

    private final SuscripcionService suscripcionService;

    public SuscripcionController(SuscripcionService suscripcionService) {
        this.suscripcionService = suscripcionService;
    }

    @GetMapping("/actual")
    @PreAuthorize("hasAuthority('SUSCRIPCION_VER')")
    public ResponseEntity<SuscripcionResponseDTO> obtenerActual() {
        return ResponseEntity.ok(suscripcionService.obtenerActual());
    }

    @GetMapping("/actual/accesos")
    @PreAuthorize("hasAuthority('SUSCRIPCION_VER')")
    public ResponseEntity<PlanAccessResponseDTO> obtenerAccesosActuales() {
        return ResponseEntity.ok(suscripcionService.obtenerAccesosActuales());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SUSCRIPCION_GESTIONAR')")
    public ResponseEntity<SuscripcionResponseDTO> adquirirPlan(@RequestBody @Valid SuscripcionCompraRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(suscripcionService.adquirirPlan(dto));
    }
}
