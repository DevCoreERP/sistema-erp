package com.devcoreerp.backend_erp.auth.infrastructure.dtos;

public record PermissionDTO(
    Long id,
    String code,
    String description,
    String category
) {}