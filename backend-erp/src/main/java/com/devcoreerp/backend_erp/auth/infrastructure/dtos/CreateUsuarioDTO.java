package com.devcoreerp.backend_erp.auth.infrastructure.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUsuarioDTO(
    @NotBlank(message = "El nombre de usuario no puede estar vacío")
    @Size(min = 3, max = 100, message = "El nombre de usuario debe tener entre 3 y 100 caracteres")
    String username,
    
    @NotBlank(message = "La contraseña no puede estar vacía")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    String password,
    
    @NotBlank(message = "El primer nombre no puede estar vacío")
    String firstName,
    
    @NotBlank(message = "El apellido no puede estar vacío")
    String surnames,
    
    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "El email debe ser válido")
    String email,
    
    @NotBlank(message = "El teléfono no puede estar vacío")
    String phoneNumber,
    
    @NotBlank(message = "El rol no puede estar vacío")
    String roleName
) {}
