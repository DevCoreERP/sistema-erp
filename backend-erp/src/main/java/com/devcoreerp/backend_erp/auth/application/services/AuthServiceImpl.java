package com.devcoreerp.backend_erp.auth.application.services;

import com.devcoreerp.backend_erp.auth.domain.Role;
import com.devcoreerp.backend_erp.auth.domain.Usuario;
import com.devcoreerp.backend_erp.auth.domain.services.AuthService;
import com.devcoreerp.backend_erp.auth.domain.services.TokenService;
import com.devcoreerp.backend_erp.auth.infrastructure.dtos.CreateUsuarioDTO;
import com.devcoreerp.backend_erp.auth.infrastructure.dtos.LoginRequestDTO;
import com.devcoreerp.backend_erp.auth.infrastructure.dtos.UsuarioResponseDTO;
import com.devcoreerp.backend_erp.auth.infrastructure.persistance.RoleRepository;
import com.devcoreerp.backend_erp.auth.infrastructure.persistance.UsuarioRepository;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class AuthServiceImpl implements AuthService, UserDetailsService {

    private static final Logger logger = LogManager.getLogger(AuthServiceImpl.class);

    private final UsuarioRepository usuarioRepository;
    private final RoleRepository roleRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationConfiguration authenticationConfiguration;

    public AuthServiceImpl(
            UsuarioRepository usuarioRepository,
            RoleRepository roleRepository,
            TokenService tokenService,
            PasswordEncoder passwordEncoder,
            AuthenticationConfiguration authenticationConfiguration) {
        this.usuarioRepository = usuarioRepository;
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
                loginRequest.password()
            );

            Authentication authentication = authenticationManager.authenticate(authRequest);
            Usuario usuario = (Usuario) authentication.getPrincipal();

            if (!usuario.isEnabled()) {
                throw new DisabledException("Usuario inactivo");
            }

            String token = tokenService.generateToken(authentication);
            logger.info("[AUTH] Login exitoso para usuario: {}", usuario.getUsername());
            return token;
        } catch (DisabledException e) {
            logger.warn("[AUTH] Login rechazado por usuario inactivo: {}", loginRequest.email());
            throw e;
        } catch (Exception e) {
            logger.error("[AUTH] Error en login", e);
            throw new BadCredentialsException("Credenciales invalidas");
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
        createUsuarioAndReturn(createUsuarioDTO);
    }

    public UsuarioResponseDTO createUsuarioAndReturn(CreateUsuarioDTO createUsuarioDTO) {
        if (usuarioRepository.existsByEmail(createUsuarioDTO.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un usuario con ese email");
        }

        if (usuarioRepository.existsByUsername(createUsuarioDTO.username())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un usuario con ese nombre de usuario");
        }

        Role role = roleRepository.findByName(createUsuarioDTO.roleName().trim())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Rol no encontrado: " + createUsuarioDTO.roleName()
            ));

        if (!Boolean.TRUE.equals(role.getEstado())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se puede asignar un rol inactivo");
        }

        Usuario usuario = Usuario.builder()
            .username(createUsuarioDTO.username().trim())
            .password(passwordEncoder.encode(createUsuarioDTO.password()))
            .email(createUsuarioDTO.email().trim().toLowerCase())
            .firstName(createUsuarioDTO.firstName().trim())
            .surnames(createUsuarioDTO.surnames().trim())
            .phoneNumber(createUsuarioDTO.phoneNumber().trim())
            .fechaIngreso(new Date())
            .estado(true)
            .enabled(true)
            .accountNonExpired(true)
            .accountNonLocked(true)
            .credentialsNonExpired(true)
            .build();
        usuario.getRoles().add(role);

        Usuario savedUsuario = usuarioRepository.save(usuario);
        logger.info("[AUTH] Usuario creado exitosamente: {} (ID: {})", savedUsuario.getUsername(), savedUsuario.getId());
        return mapToResponseDTO(savedUsuario);
    }

    public UsuarioResponseDTO assignRoleToUser(Long usuarioId, Long roleId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        Role role = roleRepository.findById(roleId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rol no encontrado"));

        if (!Boolean.TRUE.equals(usuario.getEstado()) || !Boolean.TRUE.equals(usuario.getEnabled())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se puede asignar rol a un usuario inactivo");
        }

        if (!Boolean.TRUE.equals(role.getEstado())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se puede asignar un rol inactivo");
        }

        boolean alreadyAssigned = usuario.getRoles().stream()
            .anyMatch(existing -> existing.getId().equals(role.getId()));
        if (alreadyAssigned) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El rol ya esta asignado al usuario");
        }

        usuario.getRoles().add(role);
        return mapToResponseDTO(usuarioRepository.save(usuario));
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario getUsuario(Long id) {
        return usuarioRepository.findWithRolesById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario getUsuarioByUsername(String username) {
        return usuarioRepository.findByUsername(username)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado: " + username));
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> {
                logger.error("[AUTH] Usuario no encontrado con email: {}", email);
                return new UsernameNotFoundException("Usuario no encontrado");
            });

        if (!usuario.isEnabled()) {
            throw new DisabledException("Usuario inactivo");
        }

        return usuario;
    }

    private UsuarioResponseDTO mapToResponseDTO(Usuario usuario) {
        Set<String> roleNames = usuario.getRoles().stream()
            .filter(role -> Boolean.TRUE.equals(role.getEstado()))
            .map(Role::getName)
            .collect(Collectors.toSet());

        return new UsuarioResponseDTO(
            usuario.getId(),
            usuario.getUsername(),
            usuario.getFirstName(),
            usuario.getSurnames(),
            usuario.getEmail(),
            usuario.getPhoneNumber(),
            usuario.getEstado(),
            roleNames
        );
    }

    public Set<String> getCurrentPermissionCodes(Usuario usuario) {
        return usuario.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toSet());
    }
}
