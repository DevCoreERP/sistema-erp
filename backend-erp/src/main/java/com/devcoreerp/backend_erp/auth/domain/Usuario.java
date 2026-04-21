package com.devcoreerp.backend_erp.auth.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;

/**
 * Entidad Usuario: Maneja credenciales de autenticación y la relación con Rol.
 * Separada de Persona para mantener responsabilidades claras.
 * 
 * Relación:
 * - 1:1 con Persona (datos civiles)
 * - N:1 con Role (un usuario tiene un rol)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "usuarios")
public class Usuario implements UserDetails {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false, length = 100)
    private String username;
    
    @Column(nullable = false)
    private String password;
    
    /**
     * Relación 1:1 con Persona (datos civiles/personales)
     */
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "persona_id", unique = true, nullable = false)
    private Persona persona;
    
    /**
     * Relación N:1 con Role
     * Un usuario tiene un rol, pero un rol puede estar en múltiples usuarios
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;
    
    @Column(nullable = false)
    private Boolean accountNonExpired = true;
    
    @Column(nullable = false)
    private Boolean accountNonLocked = true;
    
    @Column(nullable = false)
    private Boolean credentialsNonExpired = true;
    
    @Column(nullable = false)
    private Boolean enabled = true;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date createdAt;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastLoginAt;
    
    // Constructor para facilitar creación
    public Usuario(String username, String password, Persona persona, Role role) {
        this.username = username;
        this.password = password;
        this.persona = persona;
        this.role = role;
        this.createdAt = new Date();
    }
    
    /**
     * Implementación de UserDetails: retorna los roles del usuario
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(role);
    }
    
    /**
     * Obtiene el nombre completo del usuario desde su Persona
     */
    public String getFullName() {
        return persona != null ? persona.getFullName() : username;
    }
    
    /**
     * Obtiene el email del usuario desde su Persona
     */
    public String getEmail() {
        return persona != null ? persona.getEmail() : null;
    }
    
    /**
     * Verifica si el usuario tiene un permiso específico
     */
    public boolean hasPermission(String permissionCode) {
        return role != null && role.hasPermission(permissionCode);
    }
    
    /**
     * Implementaciones de UserDetails
     */
    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }
    
    @Override
    public boolean isEnabled() {
        return enabled;
    }
}