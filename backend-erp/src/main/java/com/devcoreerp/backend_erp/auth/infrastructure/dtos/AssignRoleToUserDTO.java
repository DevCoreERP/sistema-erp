package com.devcoreerp.backend_erp.auth.infrastructure.dtos;

import jakarta.validation.constraints.NotNull;

public record AssignRoleToUserDTO(
    @NotNull(message = "El ID del rol es obligatorio")
    Long roleId
) {}
