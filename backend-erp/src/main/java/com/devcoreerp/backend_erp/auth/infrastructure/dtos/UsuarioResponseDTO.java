package com.devcoreerp.backend_erp.auth.infrastructure.dtos;

import java.util.Set;

public record UsuarioResponseDTO(
    Long id,
    String username,
    String firstName,
    String surnames,
    String email,
    String phoneNumber,
    Boolean estado,
    Set<String> roleNames
) {}
