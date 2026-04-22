package com.devcoreerp.backend_erp.organizational.infrastructure.dtos;

import java.util.Date;

public record ResponseCargoDTO(
    Long id,
    String nombre,
    Boolean active,
    Date createdAt,
    ResponseDepartamentoDTO departamento
){}
