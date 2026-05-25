package com.devcoreerp.backend_erp.auth.infrastructure.controllers;

import com.devcoreerp.backend_erp.auth.application.AuthCookieConstants.AuthConstants;
import com.devcoreerp.backend_erp.auth.application.services.AuthServiceImpl;
import com.devcoreerp.backend_erp.auth.domain.Usuario;
import com.devcoreerp.backend_erp.auth.infrastructure.config.ApiConfig;
import com.devcoreerp.backend_erp.auth.infrastructure.dtos.AssignRolesToUserDTO;
import com.devcoreerp.backend_erp.auth.infrastructure.dtos.CreateUsuarioDTO;
import com.devcoreerp.backend_erp.auth.infrastructure.dtos.LoginRequestDTO;
import com.devcoreerp.backend_erp.auth.infrastructure.dtos.LoginTokenResponseDTO;
import com.devcoreerp.backend_erp.auth.infrastructure.dtos.UsuarioResponseDTO;
import com.devcoreerp.backend_erp.auth.infrastructure.dtos.UsuarioUpdateDTO;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiConfig.API_BASE_PATH + "/auth")
public class AuthController {

    private static final Logger logger = LogManager.getLogger(AuthController.class);

    private final AuthServiceImpl authService;

    public AuthController(AuthServiceImpl authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody @Valid LoginRequestDTO loginRequestDTO,
            HttpServletResponse response) {
        logger.info("[AUTH] Login request para email: {}", loginRequestDTO.email());

        try {
            String token = authService.login(loginRequestDTO);
            response.addCookie(createAuthCookie(token));
            return ResponseEntity.ok(new LoginTokenResponseDTO("Login successful", token, "Bearer"));
        } catch (Exception e) {
            logger.warn("[AUTH] Login rechazado para email: {}", loginRequestDTO.email());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("{\"error\": \"Credenciales invalidas\"}");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        logger.info("[AUTH] Logout request");

        Cookie cookie = new Cookie(AuthConstants.TOKEN_COOKIE_NAME, null);
        cookie.setMaxAge(0);
        cookie.setHttpOnly(AuthConstants.HTTP_ONLY);
        cookie.setSecure(AuthConstants.COOKIE_SECURE);
        cookie.setPath("/");
        response.addCookie(cookie);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "USUARIO_CREAR", description = "Se debe asignar un rol")
    @PostMapping("/usuarios")
    @PreAuthorize("hasAuthority('USUARIO_CREAR')")
    public ResponseEntity<UsuarioResponseDTO> createUsuario(@RequestBody @Valid CreateUsuarioDTO createUsuarioDTO) {
        logger.info("[AUTH] Crear usuario: {}", createUsuarioDTO.username());
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.createUsuarioAndReturn(createUsuarioDTO));
    }

    @Operation(summary = "USUARIO_LISTAR", description = "Lista los Usuarios segun su Estado")
    @GetMapping("/usuarios")
    @PreAuthorize("hasAuthority('USUARIO_LISTAR')")
    public ResponseEntity<List<UsuarioResponseDTO>> listarUsuarios(
            @RequestParam(required = false) Boolean estado) {
        return ResponseEntity.ok(authService.listarUsuarios(estado));
    }

    // lista un usuario en especifico mediante id
    @GetMapping("/usuarios/{id}")
    @PreAuthorize("hasAuthority('USUARIO_LISTAR')")
    public ResponseEntity<UsuarioResponseDTO> getUsuario(@PathVariable Long id) {
        logger.info("[AUTH] Obtener usuario: {}", id);
        Usuario usuario = authService.getUsuario(id);
        return ResponseEntity.ok(new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getUsername(),
                usuario.getFirstName(),
                usuario.getSurnames(),
                usuario.getEmail(),
                usuario.getPhoneNumber(),
                usuario.getFechaIngreso(),
                usuario.getEstado(),
                usuario.getRoles().stream()
                        .filter(role -> Boolean.TRUE.equals(role.getEstado()))
                        .map(role -> role.getName())
                        .collect(Collectors.toSet())));
    }

    // le asigna al usuario con {id} roles [..,..,..]
    @Operation(summary = "USUARIO_ASIGNAR_ROL", description = "Asigna rol a Usuario")
    @PostMapping("/usuarios/{usuarioId}/roles")
    @PreAuthorize("hasAuthority('USUARIO_ASIGNAR_ROL')")
    public ResponseEntity<UsuarioResponseDTO> assignRoleToUser(
            @PathVariable Long usuarioId,
            @RequestBody @Valid AssignRolesToUserDTO dto) {
        logger.info("[AUTH] Asignar rol {} al usuario {}", dto.rolesId(), usuarioId);
        return ResponseEntity.ok(authService.assignRolesToUser(usuarioId, dto.rolesId()));
    }

    @Operation(summary = "USUARIO_ELIMINAR", description = "Elimina de manera logica al Usuario")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('USUARIO_ELIMINAR')")
    public ResponseEntity<Void> eliminarLogicamente(@PathVariable Long id) {
        logger.info("[USUARIO] Eliminar logicamente Usuario: {}", id);
        authService.eliminarLogicamente(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('USUARIO_EDITAR')")
    public ResponseEntity<UsuarioResponseDTO> actulizarCampoUsuario(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioUpdateDTO dto) {
        logger.info("[USUARIO] Actualiza el Campo del Usuario: {}", id);
        return ResponseEntity.ok(authService.actualizarCampoUsuario(id, dto));
    }

    @Operation(summary = "USUARIO_MIO", description = "Datos de mi Usuario")
    @GetMapping("/me")
    @PreAuthorize("hasAuthority('USUARIO_LISTAR')")
    public ResponseEntity<UsuarioResponseDTO> me(@AuthenticationPrincipal Usuario usuario) {
        logger.info("[USUARIO] Obtener mis datos: {}", usuario.getUsername());
        return ResponseEntity.ok(authService.me(usuario.getId()));
    }

    private Cookie createAuthCookie(String token) {
        Cookie cookie = new Cookie(AuthConstants.TOKEN_COOKIE_NAME, token);
        cookie.setHttpOnly(AuthConstants.HTTP_ONLY);
        cookie.setSecure(AuthConstants.COOKIE_SECURE);
        cookie.setMaxAge(AuthConstants.COOKIE_MAX_AGE);
        cookie.setPath("/");
        cookie.setAttribute("SameSite", AuthConstants.SAME_SITE);
        return cookie;
    }
}
