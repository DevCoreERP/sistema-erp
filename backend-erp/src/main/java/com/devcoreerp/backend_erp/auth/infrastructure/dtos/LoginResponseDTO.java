package com.devcoreerp.backend_erp.auth.infrastructure.dtos;

public record LoginResponseDTO(
    Long usuarioId,
    String username,
    String fullName,
    String email,
    String roleName
) {}