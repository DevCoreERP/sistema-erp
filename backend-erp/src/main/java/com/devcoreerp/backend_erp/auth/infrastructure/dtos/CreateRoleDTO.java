package com.devcoreerp.backend_erp.auth.infrastructure.dtos;

import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record CreateRoleDTO(
    @NotBlank(message = "El nombre del rol no puede estar vacío")
    String name,
    
    @NotBlank(message = "La descripción no puede estar vacía")
    String description,
    
    @NotEmpty(message = "Debe asignar al menos un permiso")
    Set<String> permissionCodes
) {}
