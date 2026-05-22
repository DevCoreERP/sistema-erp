package com.devcoreerp.backend_erp.auth.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "roles")
@EqualsAndHashCode(exclude = "permissions")
@ToString(exclude = "permissions")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 255)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleType tipo;

    @Column(nullable = false)
    private Boolean estado = true;

    /**
     * Preparado para SaaS: en el futuro este valor debe reemplazarse por una
     * relacion con Empresa/Tenant. Por ahora no se persiste para no implementar
     * multi-tenant completo en esta iteracion.
     */
    @Transient
    private Long tenantId;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "role_permissions",
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    @Builder.Default
    private Set<Permission> permissions = new HashSet<>();

    public Role(String name, String description, RoleType roleType) {
        this.name = name;
        this.description = description;
        this.tipo = roleType;
        this.estado = true;
        this.permissions = new HashSet<>();
    }

    public boolean hasPermission(String permissionCode) {
        return this.permissions.stream()
            .anyMatch(permission -> Boolean.TRUE.equals(permission.getEstado())
                && permission.getCode().equalsIgnoreCase(permissionCode));
    }

    public boolean hasAllPermissions(Set<String> permissionCodes) {
        return permissionCodes.stream().allMatch(this::hasPermission);
    }

    public boolean hasAnyPermission(Set<String> permissionCodes) {
        return permissionCodes.stream().anyMatch(this::hasPermission);
    }
}
