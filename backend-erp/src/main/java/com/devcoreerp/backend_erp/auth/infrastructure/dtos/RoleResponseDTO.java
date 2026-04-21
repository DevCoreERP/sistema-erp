package com.devcoreerp.backend_erp.auth.infrastructure.dtos;

import java.util.Set;

import com.devcoreerp.backend_erp.auth.domain.RoleType;

public record RoleResponseDTO(
    Long id,
    String name,
    String description,
    RoleType roleType,
    Boolean active,
    Set<PermissionDTO> permissions
) {}