package com.devcoreerp.backend_erp.auth.domain.services;

import java.util.List;


import com.devcoreerp.backend_erp.auth.domain.Usuario;
import com.devcoreerp.backend_erp.auth.infrastructure.dtos.CreateUsuarioDTO;
import com.devcoreerp.backend_erp.auth.infrastructure.dtos.LoginRequestDTO;
import com.devcoreerp.backend_erp.auth.infrastructure.dtos.UsuarioResponseDTO;
import com.devcoreerp.backend_erp.auth.infrastructure.dtos.UsuarioUpdateDTO;

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

    UsuarioResponseDTO createUsuarioAndReturn(CreateUsuarioDTO createUsuarioDTO);

    UsuarioResponseDTO assignRolesToUser(Long usuarioId, List<Long> roleId);
    
    /**
     * Obtiene un usuario por ID
     */
    Usuario getUsuario(Long id);
    
    /**
     * Obtiene un usuario por username
     */
    Usuario getUsuarioByUsername(String username);

    //-------------------------------------------------------
    List<UsuarioResponseDTO> listarUsuarios(Boolean estado);

    void eliminarLogicamente(Long id);

    UsuarioResponseDTO actualizarCampoUsuario(Long id, UsuarioUpdateDTO dto);

    UsuarioResponseDTO me(Long id);

}
