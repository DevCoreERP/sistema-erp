package com.devcoreerp.backend_erp.auth.infrastructure.dtos;

import jakarta.validation.constraints.NotNull;

public record AssignPermissionToRoleDTO(
    @NotNull(message = "El ID del permiso es obligatorio")
    Long permissionId
) {}
