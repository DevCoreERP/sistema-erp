package com.devcoreerp.backend_erp.auth.application.services;

import com.devcoreerp.backend_erp.auth.domain.Permission;
import com.devcoreerp.backend_erp.auth.domain.Role;
import com.devcoreerp.backend_erp.auth.domain.RoleType;
import com.devcoreerp.backend_erp.auth.infrastructure.dtos.CreateRoleDTO;
import com.devcoreerp.backend_erp.auth.infrastructure.dtos.PermissionDTO;
import com.devcoreerp.backend_erp.auth.infrastructure.dtos.RoleResponseDTO;
import com.devcoreerp.backend_erp.auth.infrastructure.dtos.UpdateRoleDTO;
import com.devcoreerp.backend_erp.auth.infrastructure.persistance.PermissionRepository;
import com.devcoreerp.backend_erp.auth.infrastructure.persistance.RoleRepository;
import java.util.HashSet;
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
public class RoleService {

    private static final Logger logger = LogManager.getLogger(RoleService.class);

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    public RoleResponseDTO createRole(CreateRoleDTO createRoleDTO) {
        String roleName = createRoleDTO.name().trim();

        if (roleRepository.existsByName(roleName)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un rol con el nombre: " + roleName);
        }

        Role role = new Role(roleName, createRoleDTO.description().trim(), RoleType.CUSTOM);
        role.setPermissions(resolveActivePermissions(createRoleDTO.permissionCodes()));

        Role savedRole = roleRepository.save(role);
        logger.info("[ROLE] Rol creado: {} (ID: {})", savedRole.getName(), savedRole.getId());

        return mapToDTO(savedRole);
    }

    public RoleResponseDTO updateRole(Long roleId, UpdateRoleDTO updateRoleDTO) {
        Role role = getActiveRoleWithPermissions(roleId);

        if (role.getTipo() == RoleType.SYSTEM) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No se pueden modificar roles del sistema");
        }

        role.setDescription(updateRoleDTO.description().trim());
        role.setPermissions(resolveActivePermissions(updateRoleDTO.permissionCodes()));

        Role updatedRole = roleRepository.save(role);
        logger.info("[ROLE] Rol actualizado: {} (ID: {})", updatedRole.getName(), updatedRole.getId());

        return mapToDTO(updatedRole);
    }

    public RoleResponseDTO assignPermissionToRole(Long roleId, Long permissionId) {
        Role role = getActiveRoleWithPermissions(roleId);
        Permission permission = permissionRepository.findById(permissionId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Permiso no encontrado"));

        if (!Boolean.TRUE.equals(permission.getEstado())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se puede asignar un permiso inactivo");
        }

        boolean alreadyAssigned = role.getPermissions().stream()
            .anyMatch(existing -> existing.getId().equals(permission.getId()));
        if (alreadyAssigned) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El permiso ya esta asignado al rol");
        }

        role.getPermissions().add(permission);
        return mapToDTO(roleRepository.save(role));
    }

    @Transactional(readOnly = true)
    public RoleResponseDTO getRoleById(Long roleId) {
        return mapToDTO(getActiveRoleWithPermissions(roleId));
    }

    @Transactional(readOnly = true)
    public RoleResponseDTO getRoleByName(String name) {
        Role role = roleRepository.findByName(name)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rol no encontrado: " + name));

        return mapToDTO(role);
    }

    @Transactional(readOnly = true)
    public Set<RoleResponseDTO> getAllActiveRoles() {
        return roleRepository.findByEstadoTrue()
            .stream()
            .map(this::mapToDTO)
            .collect(Collectors.toSet());
    }

    public void deactivateRole(Long roleId) {
        Role role = getActiveRoleWithPermissions(roleId);

        if (role.getTipo() == RoleType.SYSTEM) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No se pueden desactivar roles del sistema");
        }

        role.setEstado(false);
        roleRepository.save(role);
        logger.info("[ROLE] Rol desactivado: {} (ID: {})", role.getName(), roleId);
    }

    private Role getActiveRoleWithPermissions(Long roleId) {
        Role role = roleRepository.findWithPermissionsById(roleId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rol no encontrado"));

        if (!Boolean.TRUE.equals(role.getEstado())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El rol esta inactivo");
        }

        return role;
    }

    private Set<Permission> resolveActivePermissions(Set<String> permissionCodes) {
        Set<Permission> permissions = new HashSet<>();
        for (String permissionCode : permissionCodes) {
            String normalizedCode = PermissionService.normalizePermissionCode(permissionCode);
            Permission permission = permissionRepository.findByCode(normalizedCode)
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Permiso no encontrado: " + normalizedCode
                ));

            if (!Boolean.TRUE.equals(permission.getEstado())) {
                throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No se puede asignar un permiso inactivo: " + normalizedCode
                );
            }

            permissions.add(permission);
        }
        return permissions;
    }

    private RoleResponseDTO mapToDTO(Role role) {
        Set<PermissionDTO> permissionDTOs = role.getPermissions()
            .stream()
            .filter(permission -> Boolean.TRUE.equals(permission.getEstado()))
            .map(p -> new PermissionDTO(p.getId(), p.getCode(), p.getDescription(), p.getEstado()))
            .collect(Collectors.toSet());

        return new RoleResponseDTO(
            role.getId(),
            role.getName(),
            role.getDescription(),
            role.getTipo(),
            role.getEstado(),
            permissionDTOs
        );
    }
}
