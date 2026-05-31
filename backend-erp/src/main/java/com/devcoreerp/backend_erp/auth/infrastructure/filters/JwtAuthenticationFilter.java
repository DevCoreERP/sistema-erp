package com.devcoreerp.backend_erp.auth.infrastructure.filters;

import com.devcoreerp.backend_erp.auth.application.AuthCookieConstants.AuthConstants;
import com.devcoreerp.backend_erp.auth.domain.services.AuthService;
import com.devcoreerp.backend_erp.auth.infrastructure.config.WebSecurityConfig;
import com.devcoreerp.backend_erp.multitenancy.Tenant;
import com.devcoreerp.backend_erp.multitenancy.TenantContext;
import com.devcoreerp.backend_erp.multitenancy.TenantSchemaResolver;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final AuthService authService;
    private final UserDetailsService userDetailsService;
    private final TenantSchemaResolver tenantSchemaResolver;

    public JwtAuthenticationFilter(
            AuthService authService,
            UserDetailsService userDetailsService,
            TenantSchemaResolver tenantSchemaResolver) {
        this.authService = authService;
        this.userDetailsService = userDetailsService;
        this.tenantSchemaResolver = tenantSchemaResolver;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getRequestURI().equals(WebSecurityConfig.LOGIN_URL_MATCHER);
    }

    @Override
    protected void doFilterInternal(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull FilterChain filterChain) throws ServletException, IOException {
        try {
            Optional<String> token = getJwtFromRequest(request);

            if (token.isPresent() && authService.validateToken(token.get())) {
                Tenant tenant = resolveTenantFromToken(token.get());
                TenantContext.setCurrentTenant(tenant);
                String email = authService.getUserFromToken(token.get());
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
                );
                authenticationToken.setDetails(userDetails);
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            logger.warn("JWT invalido o usuario no autorizado: {}", e.getMessage());
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }

    private Tenant resolveTenantFromToken(String token) {
        Long tenantId = authService.getTenantIdFromToken(token);
        String tenantSubdomain = authService.getTenantSubdomainFromToken(token);

        Tenant tenant = tenantId != null
                ? tenantSchemaResolver.resolveActiveTenantById(tenantId)
                : tenantSchemaResolver.resolveActiveTenantBySubdomain(tenantSubdomain);

        if (tenantSubdomain != null && !tenantSubdomain.equals(tenant.getSubdomain())) {
            throw new IllegalArgumentException("Tenant del token no coincide con el tenant activo");
        }

        return tenant;
    }

    private Optional<String> getJwtFromRequest(HttpServletRequest request) {
        Optional<String> bearerToken = getJwtFromAuthorizationHeader(request);
        if (bearerToken.isPresent()) {
            return bearerToken;
        }

        return getJwtFromCookie(request);
    }

    private Optional<String> getJwtFromAuthorizationHeader(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return Optional.empty();
        }

        return Optional.of(authorizationHeader.substring(7).trim());
    }

    private Optional<String> getJwtFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0) {
            return Optional.empty();
        }

        return Arrays.stream(cookies)
            .filter(cookie -> cookie.getName().equals(AuthConstants.TOKEN_COOKIE_NAME))
            .map(Cookie::getValue)
            .findFirst();
    }
}
