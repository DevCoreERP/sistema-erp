package com.devcoreerp.backend_erp.auth.domain;

/**
 * Enum RoleType: Distingue entre roles del sistema (predefinidos) y roles personalizados (dinámicos).
 * 
 * SYSTEM: Roles predefinidos del ERP que no pueden ser eliminados
 * CUSTOM: Roles creados dinámicamente por usuarios con permisos suficientes
 */
public enum RoleType {
    SYSTEM("Sistema predefinido"),
    CUSTOM("Rol personalizado");
    
    private final String description;
    
    RoleType(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}