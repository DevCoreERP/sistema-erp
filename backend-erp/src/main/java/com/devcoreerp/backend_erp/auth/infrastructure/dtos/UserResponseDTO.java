package com.devcoreerp.backend_erp.auth.infrastructure.dtos;

import com.devcoreerp.backend_erp.auth.domain.Role;

public record UserResponseDTO(
    
    Long id,
    String name,
    String email,
    Role role
) {
}