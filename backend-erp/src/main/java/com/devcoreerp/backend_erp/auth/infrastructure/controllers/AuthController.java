package com.devcoreerp.backend_erp.auth.infrastructure.controllers;

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
        final Cookie cookie = new Cookie(AuthConstants.TOKEN_COOKIE_NAME, StringUtils.EMPTY);
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