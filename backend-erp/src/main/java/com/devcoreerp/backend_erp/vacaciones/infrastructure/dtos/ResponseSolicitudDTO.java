package com.devcoreerp.backend_erp.vacaciones.infrastructure.dtos;

import java.util.Date;
import java.time.LocalDate;

public record ResponseSolicitudDTO(
    Long id,
    String estado,
    LocalDate fechaInicio,
    LocalDate fechaFin,
    Date createdAt,
    Date UpdatedAt,
    Long saldo
){}
