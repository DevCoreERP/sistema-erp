package com.devcoreerp.backend_erp.auth.domain.services;

import com.devcoreerp.backend_erp.auth.domain.Usuario;
import com.devcoreerp.backend_erp.auth.infrastructure.dtos.CreateUsuarioDTO;
import com.devcoreerp.backend_erp.auth.infrastructure.dtos.LoginRequestDTO;

public interface AuthService {
    
    /**
     * Realiza el login del usuario
     */
    String login(LoginRequestDTO loginRequest);
    
    /**
     * Valida un token JWT
     */
    boolean validateToken(String token);
    
    /**
     * Obtiene el email del usuario desde un token
     */
    String getUserFromToken(String token);
    
    /**
     * Crea un nuevo usuario
     */
    void createUsuario(CreateUsuarioDTO createUsuarioDTO);
    
    /**
     * Obtiene un usuario por ID
     */
    Usuario getUsuario(Long id);
    
    /**
     * Obtiene un usuario por username
     */
    Usuario getUsuarioByUsername(String username);
}
