package com.devcoreerp.backend_erp.auth.domain.services;

import java.util.Set;
import org.springframework.security.core.Authentication;

public interface TokenService {
    String generateToken(Authentication authentication);
    String getUserFromToken(String token);
    boolean validateToken(String token);
    Long getTenantIdFromToken(String token);
    String getTenantSubdomainFromToken(String token);
    Set<String> getRolesFromToken(String token);
    Set<String> getPermissionsFromToken(String token);
}
