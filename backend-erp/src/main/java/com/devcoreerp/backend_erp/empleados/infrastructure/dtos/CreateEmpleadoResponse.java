package com.devcoreerp.backend_erp.empleados.infrastructure.dtos;

import java.util.Date;

/**
 * DTO de respuesta al crear un Empleado.
 * Contiene información completa del Empleado, Persona y Usuario (si aplica)
 */
public record CreateEmpleadoResponse(
    EmpleadoData empleado,
    PersonaData persona,
    UsuarioData usuario
) {
    
    /**
     * Datos del Empleado
     */
    public record EmpleadoData(
        Long id,
        String codigoEmpleado,
        Date fechaIngreso,
        Boolean estado,
        Date createdAt
    ) {}
    
    /**
     * Datos de la Persona
     */
    public record PersonaData(
        Long id,
        String firstName,
        String surnames,
        String fullName,
        String documentType,
        String documentNumber,
        String email,
        String phoneNumber,
        String address,
        Date birthDate
    ) {}
    
    /**
     * Datos del Usuario (puede ser null si no se crearon credenciales)
     */
    public record UsuarioData(
        Long id,
        String username,
        String roleName,
        Date createdAt
    ) {}
}