package com.devcoreerp.backend_erp.organizational.infrastructure.dtos;

import java.util.Date;

public record ResponseAreaDTO(
    Long id,
    String nombre,
    Boolean active,
    Date createdAt
){}
