package com.devcoreerp.backend_erp.multitenancy;

import com.devcoreerp.backend_erp.auth.infrastructure.config.WebSecurityConfig;
import com.devcoreerp.backend_erp.multitenancy.exceptions.TenantException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

public class TenantLoginFilter extends OncePerRequestFilter {

    private final TenantResolver tenantResolver;
    private final TenantSchemaResolver tenantSchemaResolver;

    public TenantLoginFilter(TenantResolver tenantResolver, TenantSchemaResolver tenantSchemaResolver) {
        this.tenantResolver = tenantResolver;
        this.tenantSchemaResolver = tenantSchemaResolver;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !HttpMethod.POST.matches(request.getMethod())
                || !request.getRequestURI().equals(WebSecurityConfig.LOGIN_URL_MATCHER);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        try {
            String subdomain = tenantResolver.resolveSubdomain(request);
            Tenant tenant = tenantSchemaResolver.resolveActiveTenantBySubdomain(subdomain);
            TenantContext.setCurrentTenant(tenant);
            filterChain.doFilter(request, response);
        } catch (TenantException exception) {
            writeTenantError(response, exception);
        } finally {
            TenantContext.clear();
        }
    }

    private void writeTenantError(HttpServletResponse response, TenantException exception) throws IOException {
        response.setStatus(exception.getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("{\"error\": \"" + exception.getMessage() + "\"}");
    }
}
