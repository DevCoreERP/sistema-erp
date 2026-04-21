package com.devcoreerp.backend_erp.auth.infrastructure.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotación @RequirePermissions: Valida múltiples permisos con lógica AND/OR.
 * 
 * Uso:
 * @RequirePermissions(
 *     value = {"PRODUCT_CREATE", "PRODUCT_EDIT"},
 *     requireAll = true  // Requiere TODOS los permisos
 * )
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequirePermissions {
    
    /**
     * Códigos de permisos requeridos
     */
    String[] value();
    
    /**
     * Si true: requiere TODOS los permisos (AND)
     * Si false: requiere AL MENOS UNO (OR)
     */
    boolean requireAll() default false;
    
    /**
     * Descripción de los permisos requeridos
     */
    String description() default "";
}