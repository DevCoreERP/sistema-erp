package com.devcoreerp.backend_erp.auth.infrastructure.dtos;

import java.util.List;

import jakarta.validation.constraints.NotNull;

public record AssignPermissionToRoleDTO(
    @NotNull(message = "El ID del permiso es obligatorio")
    List<Long> permissionIds
) {}
