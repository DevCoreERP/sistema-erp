package com.devcoreerp.backend_erp.auth.infrastructure.config;

import com.devcoreerp.backend_erp.auth.application.AuthCookieConstants.AuthConstants;
import com.devcoreerp.backend_erp.auth.application.services.EffectivePermissionService;
import com.devcoreerp.backend_erp.auth.domain.services.AuthService;
import com.devcoreerp.backend_erp.auth.infrastructure.filters.JwtAuthenticationFilter;
import com.devcoreerp.backend_erp.multitenancy.TenantLoginFilter;
import com.devcoreerp.backend_erp.multitenancy.TenantResolver;
import com.devcoreerp.backend_erp.multitenancy.TenantSchemaResolver;
import jakarta.servlet.Filter;
import jakarta.servlet.http.Cookie;
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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity(debug = false)
@EnableMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

    private final AuthService authService;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final TenantResolver tenantResolver;
    private final TenantSchemaResolver tenantSchemaResolver;
    private final EffectivePermissionService effectivePermissionService;

    public WebSecurityConfig(
            AuthService authService,
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder,
            TenantResolver tenantResolver,
            TenantSchemaResolver tenantSchemaResolver,
            EffectivePermissionService effectivePermissionService) {
        this.authService = authService;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.tenantResolver = tenantResolver;
        this.tenantSchemaResolver = tenantSchemaResolver;
        this.effectivePermissionService = effectivePermissionService;
    }

    public static final String LOGIN_URL_MATCHER = ApiConfig.API_BASE_PATH + "/auth/login";
    public static final String LOG_OUT_URL_MATCHER = ApiConfig.API_BASE_PATH + "/auth/logout";
    public static final String TENANTS_URL_MATCHER = ApiConfig.API_BASE_PATH + "/tenants";
    public static final String BASE_URL_MATCHER = ApiConfig.API_BASE_PATH + "/**";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        final Filter jwtFilter = jwtAuthenticationFilter();

        http
            .cors(Customizer.withDefaults())
            .csrf(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(requests -> requests
                .requestMatchers(HttpMethod.POST, LOGIN_URL_MATCHER).permitAll()
                .requestMatchers(HttpMethod.POST, TENANTS_URL_MATCHER).permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                .requestMatchers(BASE_URL_MATCHER).authenticated()
                .anyRequest().denyAll()
            )
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher(LOG_OUT_URL_MATCHER, HttpMethod.POST.name()))
                .logoutSuccessHandler((request, response, authentication) -> {
                    response.setStatus(HttpStatus.NO_CONTENT.value());
                    Cookie cookie = new Cookie(AuthConstants.TOKEN_COOKIE_NAME, null);
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                })
            )
            .addFilterBefore(tenantLoginFilter(), LogoutFilter.class)
            .addFilterBefore(jwtFilter, LogoutFilter.class)
            .authenticationManager(authenticationManager())
            .exceptionHandling(handler -> handler
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\": \"Unauthorized\"}");
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\": \"Access Denied\"}");
                })
            );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);

        ProviderManager providerManager = new ProviderManager(authenticationProvider);
        providerManager.setEraseCredentialsAfterAuthentication(true);
        return providerManager;
    }

    private JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(authService, userDetailsService, tenantSchemaResolver, effectivePermissionService);
    }

    private TenantLoginFilter tenantLoginFilter() {
        return new TenantLoginFilter(tenantResolver, tenantSchemaResolver);
    }
}
