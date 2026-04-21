package com.devcoreerp.backend_erp.auth.application.mappers;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import com.devcoreerp.backend_erp.auth.domain.Usuario;
import com.devcoreerp.backend_erp.auth.domain.Persona;
import com.devcoreerp.backend_erp.auth.domain.Role;
import com.devcoreerp.backend_erp.auth.infrastructure.dtos.CreateUsuarioDTO;
import com.devcoreerp.backend_erp.auth.infrastructure.dtos.LoginRequestDTO;
import com.devcoreerp.backend_erp.auth.infrastructure.dtos.UsuarioResponseDTO;

import java.util.Date;

public class AuthMapper {

    private AuthMapper() {
        throw new UnsupportedOperationException("Clase de utilidad");
    }

    /**
     * Mapea el DTO a la entidad Usuario.
     * Importante: El Role y la Persona deben ser gestionados por el Service 
     * antes de persistir, aquí preparamos la estructura base.
     */
    public static Usuario toEntity(CreateUsuarioDTO dto, Persona persona, Role role) {
        return Usuario.builder()
                .username(dto.username())
                .password(dto.password()) // Se encriptará en el Service
                .persona(persona)         // Pasamos la persona ya creada/mapeada
                .role(role)               // Pasamos el rol recuperado de DB
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .createdAt(new Date())
                .build();
    }

    /**
     * Mapea la entidad Usuario al DTO de respuesta.
     * Accede a los datos de Persona mediante los métodos delegados en Usuario.
     */
    public static UsuarioResponseDTO toResponseDto(Usuario entity) {
        return new UsuarioResponseDTO(
                entity.getId(),
                entity.getUsername(),
                entity.getPersona().getFirstName(), // Acceso directo a persona
                entity.getPersona().getSurnames(),
                entity.getEmail(),                  // Usando el método delegado que creaste
                entity.getPersona().getPhoneNumber(),
                entity.getRole().getName()
        );
    }

    public static Authentication toAuthentication(LoginRequestDTO dto) {
        return new UsernamePasswordAuthenticationToken(
                dto.email(), 
                dto.password()
        );
    }
}