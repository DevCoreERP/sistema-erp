package com.devcoreerp.backend_erp.auth.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

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

    @Column(unique = true, nullable = false, length = 150)
    private String email;

    @Column(nullable = false, length = 100)
    private String firstName;

    @Column(nullable = false, length = 100)
    private String surnames;

    @Column(length = 20)
    private String phoneNumber;

    @Temporal(TemporalType.DATE)
    private Date fechaIngreso;

    @Column(nullable = false)
    private Boolean estado = true;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "usuario_roles",
        joinColumns = @JoinColumn(name = "usuario_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    @Column(nullable = false)
    private Boolean accountNonExpired = true;

    @Column(nullable = false)
    private Boolean accountNonLocked = true;

    @Column(nullable = false)
    private Boolean credentialsNonExpired = true;

    @Column(nullable = false)
    private Boolean enabled = true;

    public Usuario(String username, String password, String email, String firstName, String surnames, String phoneNumber) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.surnames = surnames;
        this.phoneNumber = phoneNumber;
        this.fechaIngreso = new Date();
        this.estado = true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (roles == null) {
            return Set.of();
        }

        return roles.stream()
            .filter(role -> Boolean.TRUE.equals(role.getEstado()))
            .flatMap(role -> role.getPermissions().stream())
            .filter(permission -> Boolean.TRUE.equals(permission.getEstado()))
            .map(Permission::getCode)
            .map(String::toUpperCase)
            .distinct()
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toSet());
    }

    public String getFullName() {
        return (firstName != null && surnames != null) ? firstName + " " + surnames : username;
    }

    public boolean hasPermission(String permissionCode) {
        return getAuthorities().stream()
            .anyMatch(authority -> authority.getAuthority().equalsIgnoreCase(permissionCode));
    }

    @Override
    public boolean isAccountNonExpired() {
        return Boolean.TRUE.equals(accountNonExpired);
    }

    @Override
    public boolean isAccountNonLocked() {
        return Boolean.TRUE.equals(accountNonLocked);
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return Boolean.TRUE.equals(credentialsNonExpired);
    }

    @Override
    public boolean isEnabled() {
        return Boolean.TRUE.equals(enabled) && Boolean.TRUE.equals(estado);
    }
}
