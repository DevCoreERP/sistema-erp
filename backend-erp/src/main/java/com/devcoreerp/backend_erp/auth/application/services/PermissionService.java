package com.devcoreerp.backend_erp.auth.application.services;

import com.devcoreerp.backend_erp.auth.domain.Permission;
import com.devcoreerp.backend_erp.auth.infrastructure.dtos.CreatePermissionDTO;
import com.devcoreerp.backend_erp.auth.infrastructure.dtos.PermissionDTO;
import com.devcoreerp.backend_erp.auth.infrastructure.persistance.PermissionRepository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * PermissionService: Servicio para gestionar permisos globales.
 * Los permisos son constantes del sistema y típicamente se crean durante la inicialización.
 */
@Service
@Transactional
public class PermissionService {
    
    private static final Logger logger = LogManager.getLogger(PermissionService.class);
    
    private final PermissionRepository permissionRepository;
    
    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }
    
    /**
     * Crea un nuevo permiso
     */
    public PermissionDTO createPermission(CreatePermissionDTO createPermissionDTO) {
        // Validar que no exista un permiso con ese código
        if (permissionRepository.existsByCode(createPermissionDTO.code())) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Ya existe un permiso con el código: " + createPermissionDTO.code()
            );
        }
        
        Permission permission = new Permission(
            createPermissionDTO.code(),
            createPermissionDTO.description(),
            createPermissionDTO.category()
        );
        
        Permission savedPermission = permissionRepository.save(permission);
        logger.info("[PERMISSION] Permiso creado: {} (ID: {})", savedPermission.getCode(), savedPermission.getId());
        
        return mapToDTO(savedPermission);
    }
    
    /**
     * Obtiene un permiso por ID
     */
    @Transactional(readOnly = true)
    public PermissionDTO getPermissionById(Long permissionId) {
        Permission permission = permissionRepository.findById(permissionId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Permiso no encontrado"
            ));
        
        return mapToDTO(permission);
    }
    
    /**
     * Obtiene un permiso por código
     */
    @Transactional(readOnly = true)
    public PermissionDTO getPermissionByCode(String code) {
        Permission permission = permissionRepository.findByCode(code)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Permiso no encontrado: " + code
            ));
        
        return mapToDTO(permission);
    }
    
    /**
     * Obtiene todos los permisos
     */
    @Transactional(readOnly = true)
    public Set<PermissionDTO> getAllPermissions() {
        return permissionRepository.findAll()
            .stream()
            .map(this::mapToDTO)
            .collect(Collectors.toSet());
    }
    
    /**
     * Obtiene permisos por categoría
     */
    @Transactional(readOnly = true)
    public Set<PermissionDTO> getPermissionsByCategory(String category) {
        return permissionRepository.findByCategory(category)
            .stream()
            .map(this::mapToDTO)
            .collect(Collectors.toSet());
    }
    
    /**
     * Mapea un Permission a su DTO
     */
    private PermissionDTO mapToDTO(Permission permission) {
        return new PermissionDTO(
            permission.getId(),
            permission.getCode(),
            permission.getDescription(),
            permission.getCategory()
        );
    }
}