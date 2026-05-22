package com.devcoreerp.backend_erp.auth.infrastructure.controllers;

import com.devcoreerp.backend_erp.auth.application.services.RoleService;
import com.devcoreerp.backend_erp.auth.infrastructure.config.ApiConfig;
import com.devcoreerp.backend_erp.auth.infrastructure.dtos.AssignPermissionToRoleDTO;
import com.devcoreerp.backend_erp.auth.infrastructure.dtos.CreateRoleDTO;
import com.devcoreerp.backend_erp.auth.infrastructure.dtos.RoleResponseDTO;
import com.devcoreerp.backend_erp.auth.infrastructure.dtos.UpdateRoleDTO;
import jakarta.validation.Valid;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiConfig.API_BASE_PATH + "/roles")
public class RoleController {

    private static final Logger logger = LogManager.getLogger(RoleController.class);

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROL_CREAR')")
    public ResponseEntity<RoleResponseDTO> createRole(@RequestBody @Valid CreateRoleDTO createRoleDTO) {
        logger.info("[ROLE] Crear rol: {}", createRoleDTO.name());
        return ResponseEntity.status(HttpStatus.CREATED).body(roleService.createRole(createRoleDTO));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROL_LISTAR')")
    public ResponseEntity<RoleResponseDTO> getRoleById(@PathVariable Long id) {
        logger.info("[ROLE] Obtener rol: {}", id);
        return ResponseEntity.ok(roleService.getRoleById(id));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROL_LISTAR')")
    public ResponseEntity<Set<RoleResponseDTO>> getAllRoles() {
        logger.info("[ROLE] Listar roles activos");
        return ResponseEntity.ok(roleService.getAllActiveRoles());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROL_EDITAR')")
    public ResponseEntity<RoleResponseDTO> updateRole(
            @PathVariable Long id,
            @RequestBody @Valid UpdateRoleDTO updateRoleDTO) {
        logger.info("[ROLE] Actualizar rol: {}", id);
        return ResponseEntity.ok(roleService.updateRole(id, updateRoleDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROL_ELIMINAR')")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        logger.info("[ROLE] Desactivar rol: {}", id);
        roleService.deactivateRole(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{roleId}/permissions")
    @PreAuthorize("hasAuthority('ROL_ASIGNAR_PERMISO')")
    public ResponseEntity<RoleResponseDTO> assignPermissionToRole(
            @PathVariable Long roleId,
            @RequestBody @Valid AssignPermissionToRoleDTO dto) {
        logger.info("[ROLE] Asignar permiso {} al rol {}", dto.permissionId(), roleId);
        return ResponseEntity.ok(roleService.assignPermissionToRole(roleId, dto.permissionId()));
    }

    @PostMapping("/{roleId}/permissions/{permissionId}")
    @PreAuthorize("hasAuthority('ROL_ASIGNAR_PERMISO')")
    public ResponseEntity<RoleResponseDTO> assignPermissionToRoleByPath(
            @PathVariable Long roleId,
            @PathVariable Long permissionId) {
        logger.info("[ROLE] Asignar permiso {} al rol {}", permissionId, roleId);
        return ResponseEntity.ok(roleService.assignPermissionToRole(roleId, permissionId));
    }
}
