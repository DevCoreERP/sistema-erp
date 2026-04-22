package com.devcoreerp.backend_erp.organizational.infrastructure.dtos;

import com.devcoreerp.backend_erp.organizational.domain.Departamento;

import java.util.Date;

public record ResponseCargoDTO(
    Long id,
    String nombre,
    Boolean active,
    Date createdAt,
    Departamento departamento
){}
