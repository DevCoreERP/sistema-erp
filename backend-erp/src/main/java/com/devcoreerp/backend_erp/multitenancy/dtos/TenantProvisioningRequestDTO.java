package com.devcoreerp.backend_erp.multitenancy.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TenantProvisioningRequestDTO(
        @NotBlank(message = "El nombre de la empresa es obligatorio")
        String name,

        @NotBlank(message = "El subdominio es obligatorio")
        String subdomain,

        @NotBlank(message = "El username del administrador es obligatorio")
        @Size(min = 3, max = 100, message = "El username debe tener entre 3 y 100 caracteres")
        String adminUsername,

        @NotBlank(message = "La contrasena del administrador es obligatoria")
        @Size(min = 6, message = "La contrasena debe tener al menos 6 caracteres")
        String adminPassword,

        @NotBlank(message = "El nombre del administrador es obligatorio")
        String adminFirstName,

        @NotBlank(message = "Los apellidos del administrador son obligatorios")
        String adminSurnames,

        @NotBlank(message = "El email del administrador es obligatorio")
        @Email(message = "El email del administrador debe ser valido")
        String adminEmail,

        @NotBlank(message = "El telefono del administrador es obligatorio")
        String adminPhoneNumber
) {
}
