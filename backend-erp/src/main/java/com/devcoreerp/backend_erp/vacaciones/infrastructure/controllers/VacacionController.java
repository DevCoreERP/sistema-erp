package com.devcoreerp.backend_erp.vacaciones.infrastructure.controllers;

import com.devcoreerp.backend_erp.auth.infrastructure.annotations.RequirePermission;
import com.devcoreerp.backend_erp.auth.infrastructure.config.ApiConfig;
import com.devcoreerp.backend_erp.vacaciones.infrastructure.dtos.CreateVacacionDTO;
import com.devcoreerp.backend_erp.vacaciones.infrastructure.dtos.ResponseVacacionDTO;
import com.devcoreerp.backend_erp.vacaciones.application.services.VacacionService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping(ApiConfig.API_BASE_PATH + "/vacaciones")
public class VacacionController {

    private final VacacionService vacacionService;

    public VacacionController(VacacionService vacacionService) {
        this.vacacionService = vacacionService;
    }

    @PostMapping
    @RequirePermission(value = "USER_CREATE")
    public ResponseEntity<?> create(@RequestBody CreateVacacionDTO dto) {
        try {
            ResponseVacacionDTO response = vacacionService.create(dto);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @GetMapping
    @RequirePermission(value = "USER_VIEW")
    public ResponseEntity<?> findAll() {
        try {
            List<ResponseVacacionDTO> response = vacacionService.findAll();
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/{id}")
    @RequirePermission(value = "USER_VIEW")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        try {
            ResponseVacacionDTO response = vacacionService.findById(id);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @PutMapping("/{id}")
    @RequirePermission(value = "USER_EDIT")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody CreateVacacionDTO dto) {
        try {
            ResponseVacacionDTO response = vacacionService.update(id, dto);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @DeleteMapping("/{id}")
    @RequirePermission(value = "USER_DELETE")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            ResponseVacacionDTO response = vacacionService.delete(id);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}