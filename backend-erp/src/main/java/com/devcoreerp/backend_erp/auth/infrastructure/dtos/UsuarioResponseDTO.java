package com.devcoreerp.backend_erp.auth.infrastructure.dtos;

import java.time.LocalDate;
import java.util.Set;

public record UsuarioResponseDTO(
    Long id,
    String username,
    String firstName,
    String surnames,
    String email,
    String phoneNumber,
    LocalDate fechaIngreso,
    Boolean estado,
    Set<String> roleNames
) {}
