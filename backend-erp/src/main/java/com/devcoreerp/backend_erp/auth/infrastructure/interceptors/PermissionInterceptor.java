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
        String className = handlerMethod.getBean().getClass().getSimpleName();
        String methodName = handlerMethod.getMethod().getName();
        String fullPath = className + "." + methodName;
        
        try {
            // Verificar anotación @RequirePermission
            RequirePermission requirePermission = handlerMethod.getMethodAnnotation(RequirePermission.class);
            if (requirePermission != null) {
                String permission = requirePermission.value();
                logger.info("[SECURITY] Validando permiso '{}' en: {}", permission, fullPath);
                
                boolean hasPermission = permissionEvaluator.evaluateRequirePermission(requirePermission);
                logger.info("[SECURITY] Resultado: {} - Permiso '{}' en: {}", 
                    hasPermission ? "✅ PERMITIDO" : "❌ DENEGADO", permission, fullPath);
                
                if (!hasPermission) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\": \"Access denied\", \"message\": \"Required permission: " + 
                        permission + "\", \"endpoint\": \"" + fullPath + "\"}");
                    return false;
                }
                return true;
            }
            
            // Verificar anotación @RequirePermissions
            RequirePermissions requirePermissions = handlerMethod.getMethodAnnotation(RequirePermissions.class);
            if (requirePermissions != null) {
                String[] permissions = requirePermissions.value();
                String logicType = requirePermissions.requireAll() ? "AND" : "OR";
                logger.info("[SECURITY] Validando {} permisos ({}) en: {}", 
                    permissions.length, logicType, fullPath);
                
                boolean hasPermissions = permissionEvaluator.evaluateRequirePermissions(requirePermissions);
                logger.info("[SECURITY] Resultado: {} - Permisos ({}) en: {}", 
                    hasPermissions ? "✅ PERMITIDO" : "❌ DENEGADO", logicType, fullPath);
                
                if (!hasPermissions) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\": \"Access denied\", \"message\": \"Required permissions (" + 
                        logicType + "): " + String.join(", ", permissions) + "\", \"endpoint\": \"" + fullPath + "\"}");
                    return false;
                }
                return true;
            }
            
            logger.debug("[SECURITY] Endpoint sin permisos requeridos (público): {}", fullPath);
            return true;
            
        } catch (Exception e) {
            logger.error("[SECURITY] Error validando permisos en: {}", fullPath, e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Internal Server Error\", \"message\": \"Permission validation failed\"}");
            return false;
        }
    }
}