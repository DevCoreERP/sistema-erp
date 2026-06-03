package com.devcoreerp.backend_erp.bitacora;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.devcoreerp.backend_erp.auth.application.services.TokenServiceImpl;

import java.io.IOException;

@Component
public class BitacoraFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(BitacoraFilter.class);

    private final BitacoraRepository bitacoraRepository;
    private TokenServiceImpl tokenServiceImpl;

    public BitacoraFilter(BitacoraRepository bitacoraRepository, TokenServiceImpl tokenServiceImpl) {
        this.bitacoraRepository = bitacoraRepository;
        this.tokenServiceImpl = tokenServiceImpl;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        try {
            filterChain.doFilter(request, response);
        } finally {
            // Wrap the audit logging in try-catch to NEVER corrupt the HTTP response.
            // If saving the bitacora fails, we just log the error and move on.
            try {
                String ip = getClientIp(request);
                String user = getUserEmail(request);

                Bitacora bitacora = new Bitacora();
                bitacora.setIp(ip);
                bitacora.setUsuario(user);
                bitacora.setEndpoint(request.getRequestURI());
                bitacora.setHttpStatus(response.getStatus());
                bitacora.setTenant(getTenant(request));

                bitacoraRepository.save(bitacora);
            } catch (Exception e) {
                logger.warn("[BITACORA] Error al registrar bitácora para {}: {}",
                        request.getRequestURI(), e.getMessage());
            }
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");

        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            // The first IP in the X-Forwarded-For chain is the original client IP
            return xForwardedFor.split(",")[0].trim();
        }

        return request.getRemoteAddr();
    }

    private String getTenant(HttpServletRequest request) {
        String xTenantSubdomain = request.getHeader("X-Tenant-Subdomain");
        if (xTenantSubdomain != null && !xTenantSubdomain.isBlank()) {
            // The header value IS the subdomain (e.g., "cainco"), not a key=value pair
            return xTenantSubdomain.trim();
        }
        return "";
    }

    private String getUserEmail(HttpServletRequest request) {
        String user = "";
        try {
            String cookieHeader = request.getHeader("Cookie");
            if (cookieHeader != null && cookieHeader.contains("auth-token=")) {
                String token = cookieHeader.split("auth-token=")[1].split(";")[0].trim();
                if (!token.isEmpty()) {
                    user = this.tokenServiceImpl.getUserFromToken(token);
                }
            }
        } catch (Exception e) {
            logger.debug("[BITACORA] No se pudo extraer el usuario del token: {}", e.getMessage());
        }
        return user;
    }
}