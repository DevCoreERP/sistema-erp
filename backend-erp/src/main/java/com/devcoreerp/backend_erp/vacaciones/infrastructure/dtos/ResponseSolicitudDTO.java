package com.devcoreerp.backend_erp.vacaciones.infrastructure.dtos;

import java.util.Date;

public record ResponseSolicitudDTO(
    Long id,
    String estado,
    Date fechaInicio,
    Date fechaFin,
    Date createdAt,
    Date UpdatedAt,
    ResponseVacacionDTO vacacion
){}
