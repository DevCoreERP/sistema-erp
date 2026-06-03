package com.devcoreerp.backend_erp.bitacora;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.devcoreerp.backend_erp.auth.application.services.TokenServiceImpl;

import java.io.IOException;

@Component
public class BitacoraFilter extends OncePerRequestFilter {

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

            String ip = getClientIp(request);
            String user = getUserEmail(request);

            Bitacora bitacora = new Bitacora();
            bitacora.setIp(ip);
            bitacora.setUsuario(user);
            bitacora.setEndpoint(request.getRequestURI());
            bitacora.setHttpStatus(response.getStatus());
            bitacora.setTenant(getTenant(request));

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

    private String getTenant(HttpServletRequest request){
        String xTenantSubdomain = request.getHeader("X-Tenant-Subdomain");
        if (xTenantSubdomain != null && !xTenantSubdomain.isBlank()){
            System.out.println("TENANT: "+xTenantSubdomain);
            return xTenantSubdomain.split("=")[1].trim();
        }
        return "";
    }
    private String getUserEmail(HttpServletRequest request){

        // Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String user = "";
        // if (authentication instanceof JwtAuthenticationToken jwtAuth) {
        //     user = jwtAuth.getToken().getSubject();
        //     System.out.println("Usuario jwt: "+user);
        // }
        String token = request.getHeader("Cookie");
        // user = this.tokenServiceImpl.getUserFromToken(token.split("=")[1]);
        System.out.println("Usuario: "+user);
        return user;
    }
}