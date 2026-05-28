package com.devcoreerp.backend_erp.vacaciones.infrastructure.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAuthority('SOLICITUD_CREAR')")
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
    @PreAuthorize("hasAuthority('SOLICITUD_LISTAR')")
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
    @PreAuthorize("hasAuthority('SOLICITUD_LISTAR')")
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
    @PreAuthorize("hasAuthority('SOLICITUD_EDITAR')")
    public ResponseEntity<?> aprobar(@PathVariable Long id) {
        try {
            ResponseSolicitudDTO response = solicitudService.aprobar(id);
            System.out.println("HELLO: Response");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SOLICITUD_EDITAR')")
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
    @PreAuthorize("hasAuthority('SOLICITUD_DELETE')")
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