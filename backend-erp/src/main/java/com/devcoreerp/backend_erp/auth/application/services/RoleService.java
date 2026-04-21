package com.devcoreerp.backend_erp.auth.application.services;

import com.devcoreerp.backend_erp.auth.domain.Role;
import com.devcoreerp.backend_erp.auth.domain.RoleType;
import com.devcoreerp.backend_erp.auth.domain.Permission;
import com.devcoreerp.backend_erp.auth.infrastructure.dtos.CreateRoleDTO;
import com.devcoreerp.backend_erp.auth.infrastructure.dtos.UpdateRoleDTO;
import com.devcoreerp.backend_erp.auth.infrastructure.persistance.PermissionRepository;
import com.devcoreerp.backend_erp.auth.infrastructure.persistance.RoleRepository;
import com.devcoreerp.backend_erp.auth.infrastructure.dtos.RoleResponseDTO;
import com.devcoreerp.backend_erp.auth.infrastructure.dtos.PermissionDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * RoleService: Servicio para gestionar roles dinámicamente.
 * Permite crear, actualizar y eliminar roles.
 * Solo usuarios con permisos de administrador pueden realizar estas operaciones.
 */
@Service
@Transactional
public class RoleService {
    
    private static final Logger logger = LogManager.getLogger(RoleService.class);
    
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    
    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }
    
    /**
     * Crea un nuevo rol personalizado
     */
    public RoleResponseDTO createRole(CreateRoleDTO createRoleDTO) {
        // Validar que no exista un rol con ese nombre
        if (roleRepository.existsByName(createRoleDTO.name())) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Ya existe un rol con el nombre: " + createRoleDTO.name()
            );
        }
        
        // Obtener los permisos
        Set<Permission> permissions = new HashSet<>();
        for (String permissionCode : createRoleDTO.permissionCodes()) {
            Permission permission = permissionRepository.findByCode(permissionCode)
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Permiso no encontrado: " + permissionCode
                ));
            permissions.add(permission);
        }
        
        // Crear el rol
        Role role = new Role(
            createRoleDTO.name(),
            createRoleDTO.description(),
            RoleType.CUSTOM
        );
        role.setPermissions(permissions);
        role.setCreatedAt(new Date());
        
        Role savedRole = roleRepository.save(role);
        logger.info("[ROLE] Rol creado: {} (ID: {})", savedRole.getName(), savedRole.getId());
        
        return mapToDTO(savedRole);
    }
    
    /**
     * Actualiza un rol existente
     */
    public RoleResponseDTO updateRole(Long roleId, UpdateRoleDTO updateRoleDTO) {
        Role role = roleRepository.findById(roleId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Rol no encontrado"
            ));
        
        // No se puede modificar roles del sistema
        if (role.getRoleType() == RoleType.SYSTEM) {
            throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "No se pueden modificar roles del sistema"
            );
        }
        
        // Actualizar descripción
        role.setDescription(updateRoleDTO.description());
        
        // Actualizar permisos
        Set<Permission> permissions = new HashSet<>();
        for (String permissionCode : updateRoleDTO.permissionCodes()) {
            Permission permission = permissionRepository.findByCode(permissionCode)
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Permiso no encontrado: " + permissionCode
                ));
            permissions.add(permission);
        }
        role.setPermissions(permissions);
        role.setUpdatedAt(new Date());
        
        Role updatedRole = roleRepository.save(role);
        logger.info("[ROLE] Rol actualizado: {} (ID: {})", updatedRole.getName(), updatedRole.getId());
        
        return mapToDTO(updatedRole);
    }
    
    /**
     * Obtiene un rol por ID
     */
    @Transactional(readOnly = true)
    public RoleResponseDTO getRoleById(Long roleId) {
        Role role = roleRepository.findById(roleId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Rol no encontrado"
            ));
        
        return mapToDTO(role);
    }
    
    /**
     * Obtiene un rol por nombre
     */
    @Transactional(readOnly = true)
    public RoleResponseDTO getRoleByName(String name) {
        Role role = roleRepository.findByName(name)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Rol no encontrado: " + name
            ));
        
        return mapToDTO(role);
    }
    
    /**
     * Obtiene todos los roles activos
     */
    @Transactional(readOnly = true)
    public Set<RoleResponseDTO> getAllActiveRoles() {
        return roleRepository.findByActiveTrue()
            .stream()
            .map(this::mapToDTO)
            .collect(Collectors.toSet());
    }
    
    /**
     * Desactiva un rol
     */
    public void deactivateRole(Long roleId) {
        Role role = roleRepository.findById(roleId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Rol no encontrado"
            ));
        
        if (role.getRoleType() == RoleType.SYSTEM) {
            throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "No se pueden desactivar roles del sistema"
            );
        }
        
        role.setActive(false);
        roleRepository.save(role);
        logger.info("[ROLE] Rol desactivado: {} (ID: {})", role.getName(), roleId);
    }
    
    /**
     * Mapea un Role a su DTO
     */
    private RoleResponseDTO mapToDTO(Role role) {
        Set<PermissionDTO> permissionDTOs = role.getPermissions()
            .stream()
            .map(p -> new PermissionDTO(p.getId(), p.getCode(), p.getDescription(), p.getCategory()))
            .collect(Collectors.toSet());
        
        return new RoleResponseDTO(
            role.getId(),
            role.getName(),
            role.getDescription(),
            role.getRoleType(),
            role.getActive(),
            permissionDTOs
        );
    }
}