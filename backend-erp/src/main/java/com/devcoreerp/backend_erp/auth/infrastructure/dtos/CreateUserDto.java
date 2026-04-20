package com.devcoreerp.backend_erp.auth.infrastructure.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateUserDto(
    
    @Email
    @NotBlank
    String email,

    @NotBlank
    String firstName,

    @NotBlank
    String password
) {
}
