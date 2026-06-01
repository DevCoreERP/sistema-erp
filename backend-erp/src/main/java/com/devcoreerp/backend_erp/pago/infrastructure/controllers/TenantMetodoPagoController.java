package com.devcoreerp.backend_erp.pago.infrastructure.controllers;

import com.devcoreerp.backend_erp.auth.infrastructure.config.ApiConfig;
import com.devcoreerp.backend_erp.pago.application.services.TenantMetodoPagoService;
import com.devcoreerp.backend_erp.pago.infrastructure.dtos.TenantMetodoPagoRequestDTO;
import com.devcoreerp.backend_erp.pago.infrastructure.dtos.TenantMetodoPagoResponseDTO;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiConfig.API_BASE_PATH + "/tenant-metodos-pago")
public class TenantMetodoPagoController {

    private final TenantMetodoPagoService tenantMetodoPagoService;

    public TenantMetodoPagoController(TenantMetodoPagoService tenantMetodoPagoService) {
        this.tenantMetodoPagoService = tenantMetodoPagoService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('METODO_PAGO_GESTIONAR')")
    public ResponseEntity<List<TenantMetodoPagoResponseDTO>> listar() {
        return ResponseEntity.ok(tenantMetodoPagoService.listarActivos());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('METODO_PAGO_GESTIONAR')")
    public ResponseEntity<TenantMetodoPagoResponseDTO> registrar(@RequestBody @Valid TenantMetodoPagoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tenantMetodoPagoService.registrar(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('METODO_PAGO_GESTIONAR')")
    public ResponseEntity<TenantMetodoPagoResponseDTO> actualizar(
            @PathVariable Long id,
            @RequestBody @Valid TenantMetodoPagoRequestDTO dto) {
        return ResponseEntity.ok(tenantMetodoPagoService.actualizar(id, dto));
    }
}
