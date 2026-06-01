package com.devcoreerp.backend_erp.pago.infrastructure.controllers;

import com.devcoreerp.backend_erp.auth.infrastructure.config.ApiConfig;
import com.devcoreerp.backend_erp.pago.application.services.PagoService;
import com.devcoreerp.backend_erp.pago.infrastructure.dtos.PagoResponseDTO;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiConfig.API_BASE_PATH + "/pagos")
public class PagoController {

    private final PagoService pagoService;

    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('PAGO_LISTAR')")
    public ResponseEntity<List<PagoResponseDTO>> listarHistorial() {
        return ResponseEntity.ok(pagoService.listarHistorialTenantActual());
    }
}
