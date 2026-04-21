package com.devcoreerp.backend_erp.auth.infrastructure.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotación @RequirePermission: Valida que el usuario tenga un permiso específico
 * para acceder a un endpoint.
 * 
 * Uso:
 * @RequirePermission("PRODUCT_CREATE")
 * public void createProduct(...) { ... }
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequirePermission {
    
    /**
     * Código del permiso requerido
     */
    String value();
    
    /**
     * Descripción del permiso (opcional, para documentación)
     */
    String description() default "";
}