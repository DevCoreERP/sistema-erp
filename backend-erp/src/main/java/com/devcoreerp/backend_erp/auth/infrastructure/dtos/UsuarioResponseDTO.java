package com.devcoreerp.backend_erp.auth.infrastructure.dtos;

public record UsuarioResponseDTO(
    Long id,
    String username,
    String firstName,
    String surnames,
    String email,
    String phoneNumber,
    String roleName
) {}