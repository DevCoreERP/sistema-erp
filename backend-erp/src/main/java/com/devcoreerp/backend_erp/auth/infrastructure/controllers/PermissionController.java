package com.devcoreerp.backend_erp.auth.infrastructure.controllers;

import com.devcoreerp.backend_erp.auth.application.services.PermissionService;
import com.devcoreerp.backend_erp.auth.infrastructure.annotations.RequirePermission;
import com.devcoreerp.backend_erp.auth.infrastructure.dtos.CreatePermissionDTO;
import com.devcoreerp.backend_erp.auth.infrastructure.dtos.PermissionDTO;
import com.devcoreerp.backend_erp.auth.infrastructure.config.ApiConfig;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Set;

/**
 * PermissionController: Maneja operaciones relacionadas con permisos.
 * Endpoints:
 * - POST /permissions - Crear permiso (requiere PERMISSION_CREATE)
 * - GET /permissions/{id} - Obtener permiso (requiere PERMISSION_VIEW)
 * - GET /permissions - Listar permisos (requiere PERMISSION_VIEW)
 * - GET /permissions/category/{category} - Listar permisos por categoría
 */
@RestController
@RequestMapping(ApiConfig.API_BASE_PATH + "/permissions")
public class PermissionController {
    
    private static final Logger logger = LogManager.getLogger(PermissionController.class);
    
    private final PermissionService permissionService;
    
    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }
    
    /**
     * Endpoint para crear un nuevo permiso
     * POST /hey-fincas-api/v1/permissions
     * Requiere permiso: PERMISSION_CREATE
     */
    @PostMapping
    @RequirePermission(value = "PERMISSION_CREATE", description = "Permiso para crear permisos")
    public ResponseEntity<?> createPermission(@RequestBody @Valid CreatePermissionDTO createPermissionDTO) {
        logger.info("[PERMISSION] Crear permiso: {}", createPermissionDTO.code());
        
        try {
            PermissionDTO response = permissionService.createPermission(createPermissionDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            logger.error("[PERMISSION] Error al crear permiso: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
    
    /**
     * Endpoint para obtener un permiso por ID
     * GET /hey-fincas-api/v1/permissions/{id}
     * Requiere permiso: PERMISSION_VIEW
     */
    @GetMapping("/{id}")
    @RequirePermission(value = "PERMISSION_VIEW", description = "Permiso para ver permisos")
    public ResponseEntity<?> getPermissionById(@PathVariable Long id) {
        logger.info("[PERMISSION] Obtener permiso: {}", id);
        
        try {
            PermissionDTO response = permissionService.getPermissionById(id);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("[PERMISSION] Error al obtener permiso: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
    
    /**
     * Endpoint para listar todos los permisos
     * GET /hey-fincas-api/v1/permissions
     * Requiere permiso: PERMISSION_VIEW
     */
    @GetMapping
    @RequirePermission(value = "PERMISSION_VIEW", description = "Permiso para ver permisos")
    public ResponseEntity<?> getAllPermissions() {
        logger.info("[PERMISSION] Listar todos los permisos");
        
        try {
            Set<PermissionDTO> response = permissionService.getAllPermissions();
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("[PERMISSION] Error al listar permisos: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
    
    /**
     * Endpoint para obtener permisos por categoría
     * GET /hey-fincas-api/v1/permissions/category/{category}
     * Requiere permiso: PERMISSION_VIEW
     */
    @GetMapping("/category/{category}")
    @RequirePermission(value = "PERMISSION_VIEW", description = "Permiso para ver permisos")
    public ResponseEntity<?> getPermissionsByCategory(@PathVariable String category) {
        logger.info("[PERMISSION] Obtener permisos por categoría: {}", category);
        
        try {
            Set<PermissionDTO> response = permissionService.getPermissionsByCategory(category);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("[PERMISSION] Error al obtener permisos: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}