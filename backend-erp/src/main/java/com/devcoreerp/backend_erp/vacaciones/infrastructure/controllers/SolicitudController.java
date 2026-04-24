package com.devcoreerp.backend_erp.vacaciones.infrastructure.controllers;

import com.devcoreerp.backend_erp.auth.infrastructure.annotations.RequirePermission;
import com.devcoreerp.backend_erp.auth.infrastructure.config.ApiConfig;
import com.devcoreerp.backend_erp.vacaciones.infrastructure.dtos.CreateSolicitudDTO;
import com.devcoreerp.backend_erp.vacaciones.infrastructure.dtos.ResponseSolicitudDTO;
import com.devcoreerp.backend_erp.vacaciones.application.services.SolicitudService;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping(ApiConfig.API_BASE_PATH + "/solicitudes")
public class SolicitudController {

    private final SolicitudService solicitudService;

    public SolicitudController(SolicitudService solicitudService) {
        this.solicitudService = solicitudService;
    }

    @PostMapping
    @RequirePermission( value = "USER_CREATE")
    public ResponseEntity<?> create(@RequestBody CreateSolicitudDTO dto) {
        try {
            ResponseSolicitudDTO response = solicitudService.create(dto);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @GetMapping
    @RequirePermission( value = "USER_VIEW")
    public ResponseEntity<?> findAll() {
        try {
            List<ResponseSolicitudDTO> response = solicitudService.findAll();
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/{id}")
    @RequirePermission( value = "USER_VIEW")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        try {
            ResponseSolicitudDTO response = solicitudService.findById(id);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    /**
     * Realiza el descuento de dias automaticamente
     */
    @PutMapping("/{id}/aprobar")
    @RequirePermission( value = "USER_EDIT")
    public ResponseEntity<?> aprobar(@PathVariable Long id, @RequestBody CreateSolicitudDTO dto) {
        try {
            ResponseSolicitudDTO response = solicitudService.aprobar(id, dto);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @PutMapping("/{id}")
    @RequirePermission( value = "USER_EDIT")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody CreateSolicitudDTO dto) {
        try {
            ResponseSolicitudDTO response = solicitudService.update(id, dto);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @DeleteMapping("/{id}")
    @RequirePermission( value = "USER_DELETE")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            ResponseSolicitudDTO response = solicitudService.delete(id);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}