package com.devcoreerp.backend_erp.vacaciones.infrastructure.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import com.devcoreerp.backend_erp.auth.infrastructure.config.ApiConfig;
import com.devcoreerp.backend_erp.vacaciones.infrastructure.dtos.CreateSaldoDTO;
import com.devcoreerp.backend_erp.vacaciones.infrastructure.dtos.ResponseSaldoDTO;
import com.devcoreerp.backend_erp.vacaciones.application.services.SaldoService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping(ApiConfig.API_BASE_PATH + "/saldos")
public class SaldoController {

    private final SaldoService saldoService;

    public SaldoController(SaldoService saldoService) {
        this.saldoService = saldoService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SALDO_CREAR')")
    public ResponseEntity<?> create(@RequestBody CreateSaldoDTO dto) {
        try {
            ResponseSaldoDTO response = saldoService.create(dto);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SALDO_LISTAR')")
    public ResponseEntity<?> findAll() {
        try {
            List<ResponseSaldoDTO> response = saldoService.findAll();
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SALDO_LISTAR')")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        try {
            ResponseSaldoDTO response = saldoService.findById(id);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SALDO_EDITAR')")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody CreateSaldoDTO dto) {
        try {
            ResponseSaldoDTO response = saldoService.update(id, dto);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SALDO_ELIMINAR')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            ResponseSaldoDTO response = saldoService.delete(id);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}