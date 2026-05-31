package com.devcoreerp.backend_erp.bitacora;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class BitacoraFilter extends OncePerRequestFilter {

    private final BitacoraRepository bitacoraRepository;

    public BitacoraFilter(BitacoraRepository bitacoraRepository) {
        this.bitacoraRepository = bitacoraRepository;
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

            String ip = getClientIp(request);

            Bitacora bitacora = new Bitacora();
            bitacora.setIp(ip);
            bitacora.setEndpoint(request.getRequestURI());
            bitacora.setHttpStatus(response.getStatus());

            bitacoraRepository.save(bitacora);
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");

        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[1].trim();
        }

        return request.getRemoteAddr();
    }
}