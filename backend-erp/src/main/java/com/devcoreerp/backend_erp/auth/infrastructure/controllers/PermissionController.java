package com.devcoreerp.backend_erp.auth.infrastructure.controllers;

import com.devcoreerp.backend_erp.auth.application.services.PermissionService;
import com.devcoreerp.backend_erp.auth.infrastructure.config.ApiConfig;
import com.devcoreerp.backend_erp.auth.infrastructure.dtos.PermissionDTO;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiConfig.API_BASE_PATH + "/permissions")
public class PermissionController {

    private static final Logger logger = LogManager.getLogger(PermissionController.class);

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    // @PostMapping
    // @PreAuthorize("hasAuthority('PERMISO_CREAR')")
    // public ResponseEntity<PermissionDTO> createPermission(@RequestBody @Valid CreatePermissionDTO createPermissionDTO) {
    //     logger.info("[PERMISSION] Crear permiso: {}", createPermissionDTO.code());
    //     return ResponseEntity.status(HttpStatus.CREATED).body(permissionService.createPermission(createPermissionDTO));
    // }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISO_LISTAR')")
    public ResponseEntity<PermissionDTO> getPermissionById(@PathVariable Long id) {
        logger.info("[PERMISSION] Obtener permiso: {}", id);
        return ResponseEntity.ok(permissionService.getPermissionById(id));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('PERMISO_LISTAR')")
    public ResponseEntity<Set<PermissionDTO>> getAllPermissions() {
        logger.info("[PERMISSION] Listar permisos activos");
        return ResponseEntity.ok(permissionService.getAllPermissions());
    }
}
