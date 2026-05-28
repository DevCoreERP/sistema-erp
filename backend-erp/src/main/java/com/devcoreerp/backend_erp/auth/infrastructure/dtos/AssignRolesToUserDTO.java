package com.devcoreerp.backend_erp.auth.infrastructure.dtos;

import java.util.List;

import jakarta.validation.constraints.NotNull;

public record AssignRolesToUserDTO(
    @NotNull(message = "El ID del rol es obligatorio")
    List<Long> rolesId
) {}
