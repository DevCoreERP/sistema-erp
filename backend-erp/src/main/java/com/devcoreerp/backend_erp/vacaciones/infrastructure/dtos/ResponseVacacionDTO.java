package com.devcoreerp.backend_erp.vacaciones.infrastructure.dtos;

import com.devcoreerp.backend_erp.empleados.infrastructure.dtos.EmpleadoResponse;

import java.util.Date;

public record ResponseVacacionDTO(
    Long id,
    Long dias,
    Date createdAt,
    Date updatedAt,
    EmpleadoResponse empleado
){}
