package com.devcoreerp.backend_erp.auth.application.services;

import com.devcoreerp.backend_erp.auth.domain.Permission;
import com.devcoreerp.backend_erp.auth.infrastructure.dtos.CreatePermissionDTO;
import com.devcoreerp.backend_erp.auth.infrastructure.dtos.PermissionDTO;
import com.devcoreerp.backend_erp.auth.infrastructure.persistance.PermissionRepository;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class PermissionService {

    private static final Logger logger = LogManager.getLogger(PermissionService.class);

    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public PermissionDTO createPermission(CreatePermissionDTO createPermissionDTO) {
        String code = normalizePermissionCode(createPermissionDTO.code());

        if (permissionRepository.existsByCode(code)) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Ya existe un permiso con el codigo: " + code
            );
        }

        Permission permission = new Permission(code, createPermissionDTO.description().trim());
        Permission savedPermission = permissionRepository.save(permission);
        logger.info("[PERMISSION] Permiso creado: {} (ID: {})", savedPermission.getCode(), savedPermission.getId());

        return mapToDTO(savedPermission);
    }

    @Transactional(readOnly = true)
    public PermissionDTO getPermissionById(Long permissionId) {
        Permission permission = permissionRepository.findById(permissionId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Permiso no encontrado"));

        return mapToDTO(permission);
    }

    @Transactional(readOnly = true)
    public PermissionDTO getPermissionByCode(String code) {
        Permission permission = permissionRepository.findByCode(normalizePermissionCode(code))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Permiso no encontrado: " + code));

        return mapToDTO(permission);
    }

    @Transactional(readOnly = true)
    public Set<PermissionDTO> getAllPermissions() {
        return permissionRepository.findByEstadoTrue()
            .stream()
            .map(this::mapToDTO)
            .collect(Collectors.toSet());
    }

    static String normalizePermissionCode(String code) {
        return code.trim().replace(' ', '_').toUpperCase(Locale.ROOT);
    }

    private PermissionDTO mapToDTO(Permission permission) {
        return new PermissionDTO(
            permission.getId(),
            permission.getCode(),
            permission.getDescription(),
            permission.getEstado()
        );
    }
}
