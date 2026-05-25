package com.devcoreerp.backend_erp.auth.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.Collection;
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

import com.devcoreerp.backend_erp.control_asistencia.domain.AsignacionTurno;

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

    @Column(length = 20, unique = true)
    private String phoneNumber;

    @Column(nullable = false)
    private LocalDate fechaIngreso;

    @Column(nullable = false)
    @Builder.Default
    private Boolean estado = true;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "usuario_roles", joinColumns = @JoinColumn(name = "usuario_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    @Builder.Default
    private Set<Role> roles = new HashSet<>();


    //Asigancion de turno
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<AsignacionTurno> asignacionesTurno = new HashSet<>();

    @Column(nullable = false)
    @Builder.Default
    private Boolean accountNonExpired = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean accountNonLocked = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean credentialsNonExpired = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean enabled = true;

    public Usuario(String username, String password, String email, String firstName, String surnames,
            String phoneNumber) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.surnames = surnames;
        this.phoneNumber = phoneNumber;
        this.fechaIngreso = LocalDate.now();
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

    // Verifica si el usuario tiene un permiso específico
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
