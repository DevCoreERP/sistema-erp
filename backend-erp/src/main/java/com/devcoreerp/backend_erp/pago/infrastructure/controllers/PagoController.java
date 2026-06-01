package com.devcoreerp.backend_erp.pago.infrastructure.controllers;

import com.devcoreerp.backend_erp.auth.infrastructure.config.ApiConfig;
import com.devcoreerp.backend_erp.pago.application.services.PagoService;
import com.devcoreerp.backend_erp.pago.infrastructure.dtos.PagoResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiConfig.API_BASE_PATH + "/pagos")
@Tag(name = "Pagos", description = "Historial de pagos simulados asociados a suscripciones del tenant")
public class PagoController {

    private final PagoService pagoService;

    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('PAGO_LISTAR')")
    @Operation(
            summary = "PAGO_LISTAR",
            description = "Lista el historial de pagos del tenant autenticado. Requiere permiso PAGO_LISTAR. Los pagos actuales son simulados y se registran al adquirir un plan.")
    public ResponseEntity<List<PagoResponseDTO>> listarHistorial() {
        return ResponseEntity.ok(pagoService.listarHistorialTenantActual());
    }
}
