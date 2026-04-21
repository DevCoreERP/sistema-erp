package com.devcoreerp.backend_erp.auth.infrastructure.controllers;

// Spring Web
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

// Validación
import jakarta.validation.Valid;

// Servlet
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Cookie;

// Tus clases
import com.devcoreerp.backend_erp.auth.domain.services.AuthService;
import com.devcoreerp.backend_erp.auth.application.AuthCookieConstants.AuthConstants;
import com.devcoreerp.backend_erp.auth.infrastructure.config.ApiConfig;
import com.devcoreerp.backend_erp.auth.infrastructure.dtos.CreateUserDto;
import com.devcoreerp.backend_erp.auth.infrastructure.dtos.LoginRequestDTO;

@RestController
@RequestMapping(ApiConfig.API_BASE_PATH + "/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping
    public void createUser(@RequestBody @Valid CreateUserDto createUserDto) {
        authService.createUser(createUserDto);
    }

    @PostMapping("/login")
    public void login(@RequestBody @Valid LoginRequestDTO loginRequestDTO, HttpServletResponse response) {
        final String token = authService.login(loginRequestDTO);
        final Cookie cookie = createAuthCookie(token);
        response.addCookie(cookie);
    }

    @PostMapping("/logout")
    public void logout(HttpServletResponse response) {
        final Cookie cookie = new Cookie(AuthConstants.TOKEN_COOKIE_NAME, null);
        cookie.setMaxAge(0);
    }

    private Cookie createAuthCookie(String token) {
        final String SAME_SITE_KEY = "SameSite";
        final Cookie cookie = new Cookie(AuthConstants.TOKEN_COOKIE_NAME, token);
        cookie.setHttpOnly(AuthConstants.HTTP_ONLY);
        cookie.setSecure(AuthConstants.COOKIE_SECURE);
        cookie.setMaxAge(AuthConstants.COOKIE_MAX_AGE);
        cookie.setAttribute(SAME_SITE_KEY, AuthConstants.SAME_SITE);
        return cookie;
    }
}