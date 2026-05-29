package com.devcoreerp.backend_erp.auth.infrastructure.dtos;

public record LoginTokenResponseDTO(
    String message,
    String token,
    String tokenType
) {}
