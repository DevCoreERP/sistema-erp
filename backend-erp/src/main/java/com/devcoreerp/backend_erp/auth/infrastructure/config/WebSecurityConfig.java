package com.devcoreerp.backend_erp.auth.infrastructure.config;

import com.devcoreerp.backend_erp.auth.infrastructure.filters.JwtAuthenticationFilter;
import com.devcoreerp.backend_erp.auth.infrastructure.interceptors.PermissionInterceptor;
import jakarta.servlet.Filter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * WebSecurityConfig: Configuración de seguridad de Spring Security.
 * 
 * Características:
 * - JWT authentication via cookies
 * - Permission-based access control
 * - Stateless sessions
 * - CSRF disabled
 * - Role hierarchy
 */
@Configuration
@EnableWebSecurity(debug = false)
@EnableMethodSecurity
public class WebSecurityConfig implements WebMvcConfigurer {

    private final com.devcoreerp.backend_erp.auth.domain.services.AuthService authService;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final PermissionInterceptor permissionInterceptor;

    public WebSecurityConfig(
            com.devcoreerp.backend_erp.auth.domain.services.AuthService authService,
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder,
            PermissionInterceptor permissionInterceptor) {
        this.authService = authService;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.permissionInterceptor = permissionInterceptor;
    }

    // Definición de URLs
    public static final String LOGIN_URL_MATCHER = ApiConfig.API_BASE_PATH + "/auth/login";
    public static final String LOG_OUT_URL_MATCHER = ApiConfig.API_BASE_PATH + "/auth/logout";
    public static final String BASE_URL_MATCHER = ApiConfig.API_BASE_PATH + "/**";

    /**
     * Registra el interceptor de permisos
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(permissionInterceptor)
                .addPathPatterns(BASE_URL_MATCHER)
                .excludePathPatterns(LOGIN_URL_MATCHER);
    }

    /**
     * Configura el SecurityFilterChain
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        final Filter jwtFilter = jwtAuthenticationFilter();

        http

                // 🔴 1. ¡AGREGA ESTA LÍNEA AQUÍ PARA ACTIVAR EL CORS EN SPRING SECURITY!
                .cors(Customizer.withDefaults())
                // Desabilitar form login
                .formLogin(AbstractHttpConfigurer::disable)

                // Configurar autorización
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers(HttpMethod.POST, LOGIN_URL_MATCHER).permitAll()
                        .requestMatchers(BASE_URL_MATCHER).authenticated()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                        .anyRequest().denyAll())

                // Configurar logout
                .logout(logout -> logout
                        .logoutRequestMatcher(
                                new AntPathRequestMatcher(LOG_OUT_URL_MATCHER, HttpMethod.POST.name()))
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setStatus(HttpStatus.NO_CONTENT.value());
                            final var cookie = new jakarta.servlet.http.Cookie(
                                    com.devcoreerp.backend_erp.auth.application.AuthCookieConstants.AuthConstants.TOKEN_COOKIE_NAME,
                                    null);
                            cookie.setMaxAge(0);
                            response.addCookie(cookie);
                        }))

                // Agregar JWT filter
                .addFilterBefore(jwtFilter, LogoutFilter.class)

                // Configurar CSRF y sesiones
                .csrf((csrf) -> {
                    try {
                        csrf.disable()
                                .sessionManagement((sessionManagement) -> sessionManagement
                                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults()));
                    } catch (Exception e) {
                        throw new AuthenticationException("Spring Security Config Issue", e) {
                        };
                    }
                })

                // Configurar AuthenticationManager
                .authenticationManager(authenticationManager())

                // Manejo de excepciones
                .exceptionHandling(handler -> handler
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpStatus.UNAUTHORIZED.value());
                            response.getWriter().write("{\"error\": \"Unauthorized\"}");
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpStatus.FORBIDDEN.value());
                            response.getWriter().write("{\"error\": \"Access Denied\"}");
                        }));

        return http.build();
    }

    /**
     * Bean para AuthenticationManager
     */
    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);

        ProviderManager providerManager = new ProviderManager(authenticationProvider);
        providerManager.setEraseCredentialsAfterAuthentication(true);

        return providerManager;
    }

    /**
     * Bean para JwtAuthenticationFilter
     */
    private JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(authService, userDetailsService);
    }
}