package com.devcoreerp.backend_erp.auth.infrastructure.controllers;

import com.devcoreerp.backend_erp.auth.infrastructure.annotations.RequirePermission;
import com.devcoreerp.backend_erp.auth.infrastructure.dtos.LoginRequestDTO;
import com.devcoreerp.backend_erp.auth.infrastructure.dtos.CreateUsuarioDTO;
import com.devcoreerp.backend_erp.auth.infrastructure.dtos.UsuarioResponseDTO;
import com.devcoreerp.backend_erp.auth.infrastructure.config.ApiConfig;
import com.devcoreerp.backend_erp.auth.domain.Usuario;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Cookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.devcoreerp.backend_erp.auth.application.AuthCookieConstants.AuthConstants;

/**
 * AuthController: Maneja operaciones de autenticación y autorización.
 * Endpoints:
 * - POST /auth/login - Login sin requerimientos
 * - POST /auth/logout - Logout
 * - POST /auth/usuarios - Crear usuario (requiere permiso)
 * - GET /auth/usuarios/{id} - Obtener usuario
 */
@RestController
@RequestMapping(ApiConfig.API_BASE_PATH + "/auth")
public class AuthController {
    
    private static final Logger logger = LogManager.getLogger(AuthController.class);
    
    private final com.devcoreerp.backend_erp.auth.domain.services.AuthService authService;
    
    public AuthController(com.devcoreerp.backend_erp.auth.domain.services.AuthService authService) {
        this.authService = authService;
    }
    
    /**
     * Endpoint para login
     * POST /hey-fincas-api/v1/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody @Valid LoginRequestDTO loginRequestDTO,
            HttpServletResponse response) {
        
        logger.info("[AUTH] Login request para email: {}", loginRequestDTO.email());
        
        try {
            final String token = authService.login(loginRequestDTO);
            final Cookie cookie = createAuthCookie(token);
            response.addCookie(cookie);
            
            logger.info("[AUTH] Login exitoso");
            return ResponseEntity.ok().body("{\"message\": \"Login successful\"}");
            
        } catch (Exception e) {
            logger.error("[AUTH] Error en login: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("{\"error\": \"Credenciales inválidas\"}");
        }
    }
    
    /**
     * Endpoint para logout
     * POST /hey-fincas-api/v1/auth/logout
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        logger.info("[AUTH] Logout request");
        
        final Cookie cookie = new Cookie(AuthConstants.TOKEN_COOKIE_NAME, null);
        cookie.setMaxAge(0);
        cookie.setHttpOnly(AuthConstants.HTTP_ONLY);
        cookie.setSecure(AuthConstants.COOKIE_SECURE);
        response.addCookie(cookie);
        
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Endpoint para crear un nuevo usuario
     * POST /hey-fincas-api/v1/auth/usuarios
     * Requiere permiso: USER_CREATE
     */
    @PostMapping("/usuarios")
    @RequirePermission(value = "USER_CREATE", description = "Permiso para crear usuarios")
    public ResponseEntity<?> createUsuario(
            @RequestBody @Valid CreateUsuarioDTO createUsuarioDTO) {
        
        logger.info("[AUTH] Crear usuario: {}", createUsuarioDTO.username());
        
        try {
            authService.createUsuario(createUsuarioDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("{\"message\": \"Usuario creado exitosamente\"}");
            
        } catch (Exception e) {
            logger.error("[AUTH] Error al crear usuario: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
    
    /**
     * Endpoint para obtener un usuario por ID
     * GET /hey-fincas-api/v1/auth/usuarios/{id}
     * Requiere permiso: USER_VIEW
     */
    @GetMapping("/usuarios/{id}")
    @RequirePermission(value = "USER_VIEW", description = "Permiso para ver usuarios")
    public ResponseEntity<?> getUsuario(@PathVariable Long id) {
        
        logger.info("[AUTH] Obtener usuario: {}", id);
        
        try {
            Usuario usuario = authService.getUsuario(id);
            UsuarioResponseDTO response = new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getUsername(),
                usuario.getPersona().getFirstName(),
                usuario.getPersona().getSurnames(),
                usuario.getEmail(),
                usuario.getPersona().getPhoneNumber(),
                usuario.getRole().getName()
            );
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("[AUTH] Error al obtener usuario: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\": \"Usuario no encontrado\"}");
        }
    }
    
    /**
     * Crea una cookie de autenticación
     */
    private Cookie createAuthCookie(String token) {
        final String SAME_SITE_KEY = "SameSite";
        final Cookie cookie = new Cookie(AuthConstants.TOKEN_COOKIE_NAME, token);
        cookie.setHttpOnly(AuthConstants.HTTP_ONLY);
        cookie.setSecure(AuthConstants.COOKIE_SECURE);
        cookie.setMaxAge(AuthConstants.COOKIE_MAX_AGE);
        cookie.setPath("/");
        cookie.setAttribute(SAME_SITE_KEY, AuthConstants.SAME_SITE);
        return cookie;
    }
}