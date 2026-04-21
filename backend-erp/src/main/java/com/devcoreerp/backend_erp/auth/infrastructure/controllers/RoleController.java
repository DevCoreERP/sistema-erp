package com.devcoreerp.backend_erp.auth.infrastructure.controllers;

import com.devcoreerp.backend_erp.auth.application.services.RoleService;
import com.devcoreerp.backend_erp.auth.infrastructure.annotations.RequirePermission;
import com.devcoreerp.backend_erp.auth.infrastructure.annotations.RequirePermissions;
import com.devcoreerp.backend_erp.auth.infrastructure.dtos.CreateRoleDTO;
import com.devcoreerp.backend_erp.auth.infrastructure.dtos.UpdateRoleDTO;
import com.devcoreerp.backend_erp.auth.infrastructure.dtos.RoleResponseDTO;
import com.devcoreerp.backend_erp.auth.infrastructure.config.ApiConfig;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Set;

/**
 * RoleController: Maneja operaciones relacionadas con roles.
 * Endpoints:
 * - POST /roles - Crear rol (requiere ROLE_CREATE)
 * - GET /roles/{id} - Obtener rol (requiere ROLE_VIEW)
 * - GET /roles - Listar roles activos (requiere ROLE_VIEW)
 * - PUT /roles/{id} - Actualizar rol (requiere ROLE_EDIT)
 * - DELETE /roles/{id} - Desactivar rol (requiere ROLE_DELETE)
 */
@RestController
@RequestMapping(ApiConfig.API_BASE_PATH + "/roles")
public class RoleController {
    
    private static final Logger logger = LogManager.getLogger(RoleController.class);
    
    private final RoleService roleService;
    
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }
    
    /**
     * Endpoint para crear un nuevo rol
     * POST /hey-fincas-api/v1/roles
     * Requiere permiso: ROLE_CREATE
     */
    @PostMapping
    @RequirePermission(value = "ROLE_CREATE", description = "Permiso para crear roles")
    public ResponseEntity<?> createRole(@RequestBody @Valid CreateRoleDTO createRoleDTO) {
        logger.info("[ROLE] Crear rol: {}", createRoleDTO.name());
        
        try {
            RoleResponseDTO response = roleService.createRole(createRoleDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            logger.error("[ROLE] Error al crear rol: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
    
    /**
     * Endpoint para obtener un rol por ID
     * GET /hey-fincas-api/v1/roles/{id}
     * Requiere permiso: ROLE_VIEW
     */
    @GetMapping("/{id}")
    @RequirePermission(value = "ROLE_VIEW", description = "Permiso para ver roles")
    public ResponseEntity<?> getRoleById(@PathVariable Long id) {
        logger.info("[ROLE] Obtener rol: {}", id);
        
        try {
            RoleResponseDTO response = roleService.getRoleById(id);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("[ROLE] Error al obtener rol: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
    
    /**
     * Endpoint para listar todos los roles activos
     * GET /hey-fincas-api/v1/roles
     * Requiere permiso: ROLE_VIEW
     */
    @GetMapping
    @RequirePermission(value = "ROLE_VIEW", description = "Permiso para ver roles")
    public ResponseEntity<?> getAllRoles() {
        logger.info("[ROLE] Listar todos los roles");
        
        try {
            Set<RoleResponseDTO> response = roleService.getAllActiveRoles();
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("[ROLE] Error al listar roles: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
    
    /**
     * Endpoint para actualizar un rol
     * PUT /hey-fincas-api/v1/roles/{id}
     * Requiere permiso: ROLE_EDIT
     */
    @PutMapping("/{id}")
    @RequirePermission(value = "ROLE_EDIT", description = "Permiso para editar roles")
    public ResponseEntity<?> updateRole(
            @PathVariable Long id,
            @RequestBody @Valid UpdateRoleDTO updateRoleDTO) {
        
        logger.info("[ROLE] Actualizar rol: {}", id);
        
        try {
            RoleResponseDTO response = roleService.updateRole(id, updateRoleDTO);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("[ROLE] Error al actualizar rol: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
    
    /**
     * Endpoint para desactivar un rol
     * DELETE /hey-fincas-api/v1/roles/{id}
     * Requiere permiso: ROLE_DELETE
     */
    @DeleteMapping("/{id}")
    @RequirePermission(value = "ROLE_DELETE", description = "Permiso para eliminar roles")
    public ResponseEntity<?> deleteRole(@PathVariable Long id) {
        logger.info("[ROLE] Desactivar rol: {}", id);
        
        try {
            roleService.deactivateRole(id);
            return ResponseEntity.noContent().build();
            
        } catch (Exception e) {
            logger.error("[ROLE] Error al desactivar rol: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}