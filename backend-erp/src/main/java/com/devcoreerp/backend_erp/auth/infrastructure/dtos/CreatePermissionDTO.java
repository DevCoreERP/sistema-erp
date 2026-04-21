package com.devcoreerp.backend_erp.auth.infrastructure.dtos;

import jakarta.validation.constraints.NotBlank;

public record CreatePermissionDTO(
    @NotBlank(message = "El código del permiso no puede estar vacío")
    String code,
    
    @NotBlank(message = "La descripción no puede estar vacía")
    String description,
    
    @NotBlank(message = "La categoría no puede estar vacía")
    String category
) {}