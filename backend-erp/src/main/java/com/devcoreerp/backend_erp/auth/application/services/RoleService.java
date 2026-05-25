package com.devcoreerp.backend_erp.auth.application.services;

import com.devcoreerp.backend_erp.auth.application.mapper.RolMapper;
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
import java.util.List;
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
    private final RolMapper rolMapper;

    public RoleService(RoleRepository roleRepository,
            PermissionRepository permissionRepository,
            RolMapper rolMapper) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.rolMapper = rolMapper;
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

    public RoleResponseDTO updateRole(Long roleId, UpdateRoleDTO dto) {
        Role rol = getActiveRoleWithPermissions(roleId);

        if (rol.getTipo() == RoleType.SYSTEM) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No se pueden modificar roles del sistema");
        }

        // Validar nombre único
        if (dto.getName() != null && !dto.getName().equals(rol.getName())) {
            if (roleRepository.existsByName(dto.getName())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "El nombre ya está en uso");
            }
        }

        rolMapper.updateFromDTO(dto, rol);

        if (dto.getPermissionCodes() != null) {
            rol.setPermissions(resolveActivePermissions(dto.getPermissionCodes()));
        }

        logger.info("[ROLE] Rol actualizado: {} (ID: {})", rol.getName(), rol.getId());
        return rolMapper.toResponseDTO(rol);
    }

    public RoleResponseDTO assignPermissionToRole(Long roleId, List<Long> permissionIds) {
        Role role = getActiveRoleWithPermissions(roleId);

        // Buscar permisos
        List<Permission> permissions = permissionRepository.findAllById(permissionIds);

        // Validar que todos existan
        if (permissions.size() != permissionIds.size()) {
            throw new RuntimeException("Uno o más permisos no existen");
        }

        // Obtener IDs ya asignados
        Set<Long> existingPermissionIds = role.getPermissions()
                .stream().map(Permission::getId).collect(Collectors.toSet());

        // Filtrar solo permisos nuevos
        List<Permission> newPermissions = permissions.stream()
                .filter(permission -> !existingPermissionIds.contains(permission.getId())).toList();

        if (newPermissions.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ya tiene estos permisos");
        }

        // Agregar solo nuevos
        role.getPermissions().addAll(newPermissions);

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
                            "Permiso no encontrado: " + normalizedCode));

            if (!Boolean.TRUE.equals(permission.getEstado())) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "No se puede asignar un permiso inactivo: " + normalizedCode);
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
                permissionDTOs);
    }
}
