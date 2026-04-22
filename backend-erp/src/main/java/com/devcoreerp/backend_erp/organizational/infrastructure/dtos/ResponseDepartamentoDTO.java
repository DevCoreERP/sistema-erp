package com.devcoreerp.backend_erp.organizational.infrastructure.dtos;

import java.util.Date;

public record ResponseDepartamentoDTO(
    Long id,
    String nombre,
    Boolean active,
    Date createdAt,
    ResponseAreaDTO area
){}
