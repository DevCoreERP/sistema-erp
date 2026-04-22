package com.devcoreerp.backend_erp.empleados.infrastructure.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.Date;

/**
 * DTO para la creación de un Empleado.
 * Incluye:
 * - Datos de la Persona (obligatorios)
 * - Datos del Empleado (obligatorios)
 * - Credenciales de Usuario (opcionales)
 */
public record CreateEmpleadoRequest(
    
    // ========== DATOS DE PERSONA (Obligatorios) ==========
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    String firstName,
    
    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(max = 100, message = "Los apellidos no pueden exceder 100 caracteres")
    String surnames,
    
    @NotBlank(message = "El tipo de documento es obligatorio")
    @Size(max = 50, message = "El tipo de documento no puede exceder 50 caracteres")
    String documentType,
    
    @NotBlank(message = "El número de documento es obligatorio")
    @Size(max = 50, message = "El número de documento no puede exceder 50 caracteres")
    String documentNumber,
    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe ser válido")
    @Size(max = 150, message = "El email no puede exceder 150 caracteres")
    String email,
    
    @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
    String phoneNumber,
    
    @Size(max = 255, message = "La dirección no puede exceder 255 caracteres")
    String address,
    
    Date birthDate,
    
    // ========== DATOS DE EMPLEADO (Obligatorios) ==========
    @NotBlank(message = "El código de empleado es obligatorio")
    @Size(max = 50, message = "El código de empleado no puede exceder 50 caracteres")
    @Pattern(regexp = "^EMP-[0-9]{3,}$", message = "El código debe seguir el formato EMP-XXX")
    String codigoEmpleado,
    
    Date fechaIngreso,
    
    // ========== CREDENCIALES DE USUARIO (Opcionales) ==========
    @Valid
    UsuarioCredentials usuarioCredentials
) {
    
    /**
     * DTO anidado para las credenciales de usuario (opcional)
     */
    public record UsuarioCredentials(
        @NotBlank(message = "El username es obligatorio si se crean credenciales")
        @Size(min = 4, max = 100, message = "El username debe tener entre 4 y 100 caracteres")
        String username,
        
        @NotBlank(message = "La contraseña es obligatoria si se crean credenciales")
        @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
        String password,
        
        @NotNull(message = "El roleId es obligatorio si se crean credenciales")
        Long roleId
    ) {}
    
    /**
     * Verifica si se deben crear credenciales de usuario
     */
    public boolean shouldCreateUsuario() {
        return usuarioCredentials != null 
            && usuarioCredentials.username() != null 
            && usuarioCredentials.password() != null;
    }
}