package com.devcoreerp.backend_erp.auth.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinTable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import org.springframework.security.core.GrantedAuthority;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidad Role: Representa roles dinámicos del sistema.
 * Un usuario con rango "Dueño" puede crear y modificar roles.
 * Cada rol tiene una relación Many-to-Many con Permission.
 * 
 * Jerarquía de roles:
 * - BACK_OFFICE_ADMIN (máximo nivel)
 * - SALES_MANAGER
 * - END_USER (nivel mínimo)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "roles")
@EqualsAndHashCode(exclude = "permissions")
@ToString(exclude = "permissions")
public class Role implements GrantedAuthority {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false, length = 100)
    private String name; // ej: BACK_OFFICE_ADMIN, SALES_MANAGER, END_USER
    
    @Column(nullable = false, length = 255)
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleType roleType; // SYSTEM o CUSTOM
    
    @Column(nullable = false)
    private Boolean active = true;
    
    /**
     * Relación Many-to-Many: Un Rol puede tener múltiples Permisos
     * Un Permiso puede estar en múltiples Roles
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "role_permissions",
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    @Builder.Default
    private Set<Permission> permissions = new HashSet<>();
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date createdAt;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
    
    // Constructor alternativo para roles del sistema
    public Role(String name, String description, RoleType roleType) {
        this.name = name;
        this.description = description;
        this.roleType = roleType;
        this.active = true;
        this.createdAt = new Date();
        this.permissions = new HashSet<>();
    }
    
    /**
     * Implementación de GrantedAuthority para usar con Spring Security
     */
    @Override
    public String getAuthority() {
        return "ROLE_" + this.name.toUpperCase();
    }
    
    /**
     * Verifica si el rol tiene un permiso específico
     */
    public boolean hasPermission(String permissionCode) {
        return this.permissions.stream()
            .anyMatch(permission -> permission.getCode().equalsIgnoreCase(permissionCode));
    }
    
    /**
     * Verifica si el rol tiene todos los permisos especificados
     */
    public boolean hasAllPermissions(Set<String> permissionCodes) {
        return permissionCodes.stream()
            .allMatch(this::hasPermission);
    }
    
    /**
     * Verifica si el rol tiene al menos uno de los permisos especificados
     */
    public boolean hasAnyPermission(Set<String> permissionCodes) {
        return permissionCodes.stream()
            .anyMatch(this::hasPermission);
    }
}