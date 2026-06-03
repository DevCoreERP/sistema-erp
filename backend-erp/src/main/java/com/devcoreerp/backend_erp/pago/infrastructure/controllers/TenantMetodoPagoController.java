package com.devcoreerp.backend_erp.pago.infrastructure.controllers;

import com.devcoreerp.backend_erp.auth.infrastructure.config.ApiConfig;
import com.devcoreerp.backend_erp.pago.application.services.TenantMetodoPagoService;
import com.devcoreerp.backend_erp.pago.infrastructure.dtos.TenantMetodoPagoRequestDTO;
import com.devcoreerp.backend_erp.pago.infrastructure.dtos.TenantMetodoPagoResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Metodos de pago del tenant", description = "Registro y actualizacion de metodos de pago simulados por tenant")
public class TenantMetodoPagoController {

    private final TenantMetodoPagoService tenantMetodoPagoService;

    public TenantMetodoPagoController(TenantMetodoPagoService tenantMetodoPagoService) {
        this.tenantMetodoPagoService = tenantMetodoPagoService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('METODO_PAGO_GESTIONAR')")
    @Operation(
            summary = "METODO_PAGO_LISTAR",
            description = "Lista los metodos de pago activos registrados por el tenant autenticado. Requiere permiso METODO_PAGO_GESTIONAR.")
    public ResponseEntity<List<TenantMetodoPagoResponseDTO>> listar() {
        return ResponseEntity.ok(tenantMetodoPagoService.listarActivos());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('METODO_PAGO_GESTIONAR')")
    @Operation(
            summary = "METODO_PAGO_GESTIONAR",
            description = "Registra un metodo de pago para el tenant autenticado. No cobra dinero ni crea pago; solo guarda datos seguros como titular, referencia, marca y ultimos digitos.")
    public ResponseEntity<TenantMetodoPagoResponseDTO> registrar(@RequestBody @Valid TenantMetodoPagoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tenantMetodoPagoService.registrar(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('METODO_PAGO_GESTIONAR')")
    @Operation(
            summary = "METODO_PAGO_ACTUALIZAR",
            description = "Actualiza un metodo de pago existente del tenant autenticado. No genera pago nuevo ni cambia la suscripcion activa.")
    public ResponseEntity<TenantMetodoPagoResponseDTO> actualizar(
            @PathVariable Long id,
            @RequestBody @Valid TenantMetodoPagoRequestDTO dto) {
        return ResponseEntity.ok(tenantMetodoPagoService.actualizar(id, dto));
    }
}
