package com.devcoreerp.backend_erp.auth.infrastructure.evaluators;

import com.devcoreerp.backend_erp.auth.domain.Usuario;
import com.devcoreerp.backend_erp.auth.infrastructure.annotations.RequirePermission;
import com.devcoreerp.backend_erp.auth.infrastructure.annotations.RequirePermissions;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * PermissionEvaluator: Evalúa si un usuario tiene los permisos requeridos.
 * Se utiliza junto con los interceptores para validar acceso a endpoints.
 */
@Component
public class PermissionEvaluator {
    
    /**
     * Valida un permiso único
     */
    public boolean hasPermission(String permissionCode) {
        Usuario usuario = getCurrentUsuario();
        return usuario != null && usuario.hasPermission(permissionCode);
    }
    
    /**
     * Valida múltiples permisos con lógica AND (requiere todos)
     */
    public boolean hasAllPermissions(String... permissionCodes) {
        Usuario usuario = getCurrentUsuario();
        if (usuario == null) {
            return false;
        }
        
        Set<String> permissions = new HashSet<>(Arrays.asList(permissionCodes));
        return usuario.getRole().hasAllPermissions(permissions);
    }
    
    /**
     * Valida múltiples permisos con lógica OR (requiere al menos uno)
     */
    public boolean hasAnyPermission(String... permissionCodes) {
        Usuario usuario = getCurrentUsuario();
        if (usuario == null) {
            return false;
        }
        
        Set<String> permissions = new HashSet<>(Arrays.asList(permissionCodes));
        return usuario.getRole().hasAnyPermission(permissions);
    }
    
    /**
     * Evalúa la anotación @RequirePermission
     */
    public boolean evaluateRequirePermission(RequirePermission annotation) {
        return hasPermission(annotation.value());
    }
    
    /**
     * Evalúa la anotación @RequirePermissions
     */
    public boolean evaluateRequirePermissions(RequirePermissions annotation) {
        String[] permissions = annotation.value();
        boolean requireAll = annotation.requireAll();
        
        if (requireAll) {
            return hasAllPermissions(permissions);
        } else {
            return hasAnyPermission(permissions);
        }
    }
    
    /**
     * Obtiene el usuario actual desde el contexto de seguridad
     */
    public Usuario getCurrentUsuario() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof Usuario) {
                return (Usuario) principal;
            }
        }
        
        return null;
    }
    
    /**
     * Obtiene el ID del usuario actual
     */
    public Long getCurrentUsuarioId() {
        Usuario usuario = getCurrentUsuario();
        return usuario != null ? usuario.getId() : null;
    }
}