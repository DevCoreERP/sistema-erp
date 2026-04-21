package com.devcoreerp.backend_erp.auth.infrastructure.interceptors;

import com.devcoreerp.backend_erp.auth.infrastructure.annotations.RequirePermission;
import com.devcoreerp.backend_erp.auth.infrastructure.annotations.RequirePermissions;
import com.devcoreerp.backend_erp.auth.infrastructure.evaluators.PermissionEvaluator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * PermissionInterceptor: Intercepta las peticiones para validar permisos
 * basados en las anotaciones @RequirePermission y @RequirePermissions.
 */
@Component
public class PermissionInterceptor implements HandlerInterceptor {
    
    private static final Logger logger = LogManager.getLogger(PermissionInterceptor.class);
    
    private final PermissionEvaluator permissionEvaluator;
    
    public PermissionInterceptor(PermissionEvaluator permissionEvaluator) {
        this.permissionEvaluator = permissionEvaluator;
    }
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        
        // Verificar anotación @RequirePermission
        RequirePermission requirePermission = handlerMethod.getMethodAnnotation(RequirePermission.class);
        if (requirePermission != null) {
            if (!permissionEvaluator.evaluateRequirePermission(requirePermission)) {
                logger.warn("[SECURITY] Usuario no tiene permiso: {}", requirePermission.value());
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("{\"error\": \"Access denied. Required permission: " + 
                    requirePermission.value() + "\"}");
                return false;
            }
        }
        
        // Verificar anotación @RequirePermissions
        RequirePermissions requirePermissions = handlerMethod.getMethodAnnotation(RequirePermissions.class);
        if (requirePermissions != null) {
            if (!permissionEvaluator.evaluateRequirePermissions(requirePermissions)) {
                logger.warn("[SECURITY] Usuario no tiene los permisos requeridos");
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("{\"error\": \"Access denied. Required permissions: " + 
                    String.join(", ", requirePermissions.value()) + "\"}");
                return false;
            }
        }
        
        return true;
    }
}