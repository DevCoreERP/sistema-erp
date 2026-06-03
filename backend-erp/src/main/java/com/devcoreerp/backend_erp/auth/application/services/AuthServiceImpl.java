package com.devcoreerp.backend_erp.auth.application.services;

import com.devcoreerp.backend_erp.auth.application.mapper.UsuarioMapper;
import com.devcoreerp.backend_erp.auth.domain.Role;
import com.devcoreerp.backend_erp.auth.domain.Usuario;
import com.devcoreerp.backend_erp.auth.domain.services.AuthService;
import com.devcoreerp.backend_erp.auth.domain.services.TokenService;
import com.devcoreerp.backend_erp.auth.infrastructure.dtos.CreateUsuarioDTO;
import com.devcoreerp.backend_erp.auth.infrastructure.dtos.LoginRequestDTO;
import com.devcoreerp.backend_erp.auth.infrastructure.dtos.UsuarioResponseDTO;
import com.devcoreerp.backend_erp.auth.infrastructure.dtos.UsuarioUpdateDTO;
import com.devcoreerp.backend_erp.auth.infrastructure.persistance.RoleRepository;
import com.devcoreerp.backend_erp.auth.infrastructure.persistance.UsuarioRepository;
import com.devcoreerp.backend_erp.organizational.infrastructure.persistence.DepartamentoRepository;
import com.devcoreerp.backend_erp.multitenancy.TenantContext;

import java.time.LocalDate;
import java.util.List;
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

    private final DepartamentoRepository departamentoRepository;
    private final UsuarioRepository usuarioRepository;
    private final RoleRepository roleRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final UsuarioMapper usuarioMapper;

    public AuthServiceImpl(
            UsuarioRepository usuarioRepository,
            RoleRepository roleRepository,
            TokenService tokenService,
            PasswordEncoder passwordEncoder,
            AuthenticationConfiguration authenticationConfiguration,
            UsuarioMapper usuarioMapper,
            DepartamentoRepository departamentoRepository
    ) {
        this.departamentoRepository = departamentoRepository;
        this.usuarioRepository = usuarioRepository;
        this.roleRepository = roleRepository;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationConfiguration = authenticationConfiguration;
        this.usuarioMapper = usuarioMapper;
    }

    @Override
    public String login(LoginRequestDTO loginRequest) {
        try {
            logger.info("[AUTH] Intento de login con email: {}", loginRequest.email());

            if (TenantContext.getCurrentTenant() == null) {
                throw new BadCredentialsException("Tenant no resuelto");
            }

            AuthenticationManager authenticationManager = authenticationConfiguration.getAuthenticationManager();
            Authentication authRequest = new UsernamePasswordAuthenticationToken(
                    loginRequest.email(),
                    loginRequest.password());

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
    public Long getTenantIdFromToken(String token) {
        return tokenService.getTenantIdFromToken(token);
    }

    @Override
    public String getTenantSubdomainFromToken(String token) {
        return tokenService.getTenantSubdomainFromToken(token);
    }

    @Override
    public Set<String> getRolesFromToken(String token) {
        return tokenService.getRolesFromToken(token);
    }

    @Override
    public Set<String> getPermissionsFromToken(String token) {
        return tokenService.getPermissionsFromToken(token);
    }

    @Override
    public void createUsuario(CreateUsuarioDTO createUsuarioDTO) {
        createUsuarioAndReturn(createUsuarioDTO);
    }

    @Transactional
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
                        "Rol no encontrado: " + createUsuarioDTO.roleName()));

        if (!Boolean.TRUE.equals(role.getEstado())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se puede asignar un rol inactivo");
        }

        if (!departamentoRepository.existsById(createUsuarioDTO.departamento())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Departamento inexistente");
        }

        Usuario usuario = Usuario.builder()
                .username(createUsuarioDTO.username().trim())
                .password(passwordEncoder.encode(createUsuarioDTO.password()))
                .email(createUsuarioDTO.email().trim().toLowerCase())
                .firstName(createUsuarioDTO.firstName().trim())
                .surnames(createUsuarioDTO.surnames().trim())
                .phoneNumber(createUsuarioDTO.phoneNumber().trim())
                .departamento(createUsuarioDTO.departamento())
                .fechaIngreso(LocalDate.now())
                .estado(true)
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .build();
        usuario.getRoles().add(role);

        Usuario savedUsuario = usuarioRepository.save(usuario);
        logger.info("[AUTH] Usuario creado exitosamente: {} (ID: {})", savedUsuario.getUsername(),
                savedUsuario.getId());
        return mapToResponseDTO(savedUsuario);
    }

    @Override
    @Transactional
    public UsuarioResponseDTO assignRolesToUser(Long usuarioId, List<Long> roleIds) {

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        if (!Boolean.TRUE.equals(usuario.getEstado()) || !Boolean.TRUE.equals(usuario.getEnabled())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "No se puede asignar roles a un usuario inactivo");
        }

        List<Role> roles = roleRepository.findAllById(roleIds);

        if (roles.size() != roleIds.size()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Uno o mas roles no existen");
        }

        boolean hasInactiveRole = roles.stream().anyMatch(role -> !Boolean.TRUE.equals(role.getEstado()));

        if (hasInactiveRole) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se pueden asignar roles inactivos");
        }

        Set<Long> existingRoleIds = usuario.getRoles().stream().map(Role::getId).collect(Collectors.toSet());

        List<Role> newRoles = roles.stream().filter(role -> !existingRoleIds.contains(role.getId())).toList();

        if (newRoles.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Todos los roles ya estan asignados al usuario");
        }

        usuario.getRoles().addAll(newRoles);

        return mapToResponseDTO(usuarioRepository.save(usuario));
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario getUsuario(Long id) {
        return usuarioRepository.findWithRolesById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario getUsuarioByUsername(String username) {
        return usuarioRepository.findByUsername(username)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado: " + username));
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
                usuario.getFechaIngreso(),
                usuario.getEstado(),
                roleNames);
    }

    public Set<String> getCurrentPermissionCodes(Usuario usuario) {
        return usuario.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarUsuarios(Boolean estado) {
        List<Usuario> list = estado == null ? usuarioRepository.findAll() : usuarioRepository.findAllByEstado(estado);
        return list.stream()
                .map(usuarioMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void eliminarLogicamente(Long id) {
        Usuario usuario = buscarUsuarioPorId(id);

        // Borrado lógico: no elimina el registro físico, solo cambia su estado.
        usuario.setEstado(Boolean.FALSE);
    }

    private Usuario buscarUsuarioPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado con ID: " + id));
    }

    @Override
    @Transactional
    public UsuarioResponseDTO actualizarCampoUsuario(Long id, UsuarioUpdateDTO dto) {
        Usuario usuario = buscarUsuarioPorId(id);

        //validar que el email nose repita con otro usuario
        if (dto.getEmail() != null && !dto.getEmail().equals(usuario.getEmail())) {
            if (usuarioRepository.existsByEmail(dto.getEmail())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "El email ya está en uso");
            }
        }

        // Validar username único
        if (dto.getUsername() != null && !dto.getUsername().equals(usuario.getUsername())) {
            if (usuarioRepository.existsByUsername(dto.getUsername())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "El username ya está en uso");
            }
        }

        // Validar telelfono único
        if (dto.getPhoneNumber() != null && !dto.getPhoneNumber().equals(usuario.getPhoneNumber())) {
            if (usuarioRepository.existsByPhoneNumber(dto.getPhoneNumber())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "El telefono ya está en uso");
            }
        }

        usuarioMapper.updateFromDTO(dto, usuario); // campos simples

        // Actualizar roles solo si vienen en el request
        if (dto.getRoleNames() != null && !dto.getRoleNames().isEmpty()) {
            Set<Role> roles = dto.getRoleNames().stream()
                    .map(name -> roleRepository.findByName(name)
                            .orElseThrow(() -> new ResponseStatusException(
                                    HttpStatus.NOT_FOUND, "Rol no encontrado: " + name)))
                    .collect(Collectors.toSet());
            usuario.setRoles(roles);
        }

        return usuarioMapper.toResponseDTO(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponseDTO me(Long id) {
        Usuario usuario = usuarioRepository.findWithRolesById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado con ID: " + id));
        return usuarioMapper.toResponseDTO(usuario);
    }

}
