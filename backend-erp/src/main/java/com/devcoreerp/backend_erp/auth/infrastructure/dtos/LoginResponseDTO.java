package com.devcoreerp.backend_erp.auth.infrastructure.dtos;

import java.util.Set;

public record LoginResponseDTO(
    Long usuarioId,
    String username,
    String fullName,
    String email,
    Set<String> roleNames,
    Set<String> permissions
) {}
