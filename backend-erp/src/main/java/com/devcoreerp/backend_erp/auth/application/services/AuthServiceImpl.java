package com.devcoreerp.backend_erp.auth.application.services;

import com.devcoreerp.backend_erp.auth.domain.Usuario;
import com.devcoreerp.backend_erp.auth.domain.services.AuthService;
import com.devcoreerp.backend_erp.auth.domain.services.TokenService;
import com.devcoreerp.backend_erp.auth.domain.Persona;
import com.devcoreerp.backend_erp.auth.domain.Role;
import com.devcoreerp.backend_erp.auth.infrastructure.dtos.LoginRequestDTO;
import com.devcoreerp.backend_erp.auth.infrastructure.dtos.CreateUsuarioDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.ProviderNotFoundException;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

/**
 * Implementación de AuthService
 */
@Service
@Transactional
public class AuthServiceImpl implements AuthService, UserDetailsService {

    private static final Logger logger = LogManager.getLogger(AuthServiceImpl.class);

    private final com.devcoreerp.backend_erp.auth.infrastructure.persistance.UsuarioRepository usuarioRepository;
    private final com.devcoreerp.backend_erp.auth.infrastructure.persistance.PersonaRepository personaRepository;
    private final com.devcoreerp.backend_erp.auth.infrastructure.persistance.RoleRepository roleRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationConfiguration authenticationConfiguration;

    public AuthServiceImpl(
            com.devcoreerp.backend_erp.auth.infrastructure.persistance.UsuarioRepository usuarioRepository,
            com.devcoreerp.backend_erp.auth.infrastructure.persistance.PersonaRepository personaRepository,
            com.devcoreerp.backend_erp.auth.infrastructure.persistance.RoleRepository roleRepository,
            TokenService tokenService,
            PasswordEncoder passwordEncoder,
            AuthenticationConfiguration authenticationConfiguration) {
        this.usuarioRepository = usuarioRepository;
        this.personaRepository = personaRepository;
        this.roleRepository = roleRepository;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationConfiguration = authenticationConfiguration;
    }

    @Override
    public String login(LoginRequestDTO loginRequest) {
        try {
            logger.info("[AUTH] Intento de login con email: {}", loginRequest.email());

            AuthenticationManager authenticationManager = authenticationConfiguration.getAuthenticationManager();

            Authentication authRequest = new UsernamePasswordAuthenticationToken(
                    loginRequest.email(),
                    loginRequest.password());

            Authentication authentication = authenticationManager.authenticate(authRequest);
            String token = tokenService.generateToken(authentication);

            // Actualizar último login
            Usuario usuario = (Usuario) authentication.getPrincipal();
            usuario.setLastLoginAt(new java.util.Date());
            usuarioRepository.save(usuario);

            logger.info("[AUTH] Login exitoso para usuario: {}", usuario.getUsername());
            return token;

        } catch (Exception e) {
            logger.error("[AUTH] Error en login", e);
            throw new ProviderNotFoundException("Error al intentar iniciar sesión");
        }
    }

    @Override
    public boolean validateToken(String token) {
        return tokenService.validateToken(token);
    }

    @Override
    public String getUserFromToken(String token) {
        return tokenService.getUserFromToken(token);
    }

    @Override
    public void createUsuario(CreateUsuarioDTO createUsuarioDTO) {
        // Validar que no exista usuario con ese email
        if (usuarioRepository.existsByEmail(createUsuarioDTO.email())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya existe un usuario con ese email");
        }

        // Validar que no exista usuario con ese username
        if (usuarioRepository.existsByUsername(createUsuarioDTO.username())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya existe un usuario con ese nombre de usuario");
        }

        // Obtener el rol
        Role role = roleRepository.findByName(createUsuarioDTO.roleName())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Rol no encontrado: " + createUsuarioDTO.roleName()));

        // Crear Persona
        Persona persona = Persona.builder()
                .firstName(createUsuarioDTO.firstName())
                .surnames(createUsuarioDTO.surnames())
                .email(createUsuarioDTO.email())
                .phoneNumber(createUsuarioDTO.phoneNumber())
                .build();

        persona = personaRepository.save(persona);

        // Crear Usuario
        Usuario usuario = Usuario.builder()
                .username(createUsuarioDTO.username())
                .password(passwordEncoder.encode(createUsuarioDTO.password()))
                .persona(persona)
                .role(role)
                .build();

        usuario = usuarioRepository.save(usuario);
        logger.info("[AUTH] Usuario creado exitosamente: {} (ID: {})", usuario.getUsername(), usuario.getId());
    }

    @Override
    public Usuario getUsuario(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuario no encontrado con ID: " + id));
    }

    @Override
    public Usuario getUsuarioByUsername(String username) {
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuario no encontrado: " + username));
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("[AUTH] Usuario no encontrado con email: {}", email);
                    return new UsernameNotFoundException("Usuario no encontrado");
                });
    }

}