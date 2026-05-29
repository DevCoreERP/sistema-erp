package com.devcoreerp.backend_erp.auth.infrastructure.dtos;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UsuarioUpdateDTO {

    @Size(min = 3, max = 100, message = "El nombre de usuario debe tener entre 3 y 100 caracteres")
    private String username;

    private String firstName;

    private String surnames;

    @Email(message = "El email debe ser válido")
    private String email;

    private String phoneNumber;

    private List<String> roleNames;
}
