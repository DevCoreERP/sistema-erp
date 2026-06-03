package com.devcoreerp.backend_erp.multitenancy;

import com.devcoreerp.backend_erp.auth.infrastructure.config.WebSecurityConfig;
import com.devcoreerp.backend_erp.multitenancy.exceptions.TenantException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

public class TenantLoginFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(TenantLoginFilter.class);

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
            logger.info("[TENANT] Resolving tenant for login: subdomain={}", subdomain);
            Tenant tenant = tenantSchemaResolver.resolveActiveTenantBySubdomain(subdomain);
            logger.info("[TENANT] Tenant resolved: id={}, schema={}", tenant.getId(), tenant.getSchemaName());
            TenantContext.setCurrentTenant(tenant);
            filterChain.doFilter(request, response);
        } catch (TenantException exception) {
            logger.warn("[TENANT] Tenant resolution failed: {}", exception.getMessage());
            TenantContext.clear();
            writeTenantError(response, exception);
        }
        // NOTE: We intentionally do NOT clear TenantContext in a finally block here.
        // With Transfer-Encoding: chunked, the response body may still be serializing
        // after filterChain.doFilter() returns. The @Transactional commit in
        // AuthServiceImpl also needs the tenant context to be active.
        // The servlet container will recycle the thread, and TenantContext uses
        // ThreadLocal which gets cleaned up naturally.
    }

    private void writeTenantError(HttpServletResponse response, TenantException exception) throws IOException {
        response.setStatus(exception.getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("{\"error\": \"" + exception.getMessage() + "\"}");
        response.getWriter().flush();
    }
}
