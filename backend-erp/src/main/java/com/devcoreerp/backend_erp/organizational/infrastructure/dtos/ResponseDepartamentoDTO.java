package com.devcoreerp.backend_erp.organizational.infrastructure.dtos;

import com.devcoreerp.backend_erp.organizational.domain.Area;

import java.util.Date;

public record ResponseDepartamentoDTO(
    Long id,
    String nombre,
    Boolean active,
    Date createdAt,
    Area area
){}
