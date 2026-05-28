package com.devcoreerp.backend_erp.vacaciones.infrastructure.dtos;

import java.util.Date;

public record ResponseSaldoDTO(
    Long id,
    Long dias,
    Date createdAt,
    Date updatedAt,
    Long usuario
){}
