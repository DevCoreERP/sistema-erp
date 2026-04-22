package com.devcoreerp.backend_erp.empleados.infrastructure.dtos;

public record EmpleadoResponse(
    Long id,
    String codigoEmpleado,
    String firstName,
    String surnames,
    String email
) {}