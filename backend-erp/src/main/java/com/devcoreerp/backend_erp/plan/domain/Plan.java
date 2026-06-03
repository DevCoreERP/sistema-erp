package com.devcoreerp.backend_erp.plan.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "plan", schema = "public")
public class Plan {

    public static final String FREE_TRIAL_PLAN_NAME = "Gratis / Prueba";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 120)
    private String nombre;

    @Column(nullable = false, length = 255)
    private String descripcion;

    @Column(name = "precio_usd", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUsd;

    @Column(name = "limite_usuarios", nullable = false)
    private Integer limiteUsuarios;

    @Column(nullable = false)
    @Builder.Default
    private Boolean estado = true;

    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "plan_modulo",
            schema = "public",
            joinColumns = @JoinColumn(name = "plan_id"),
            inverseJoinColumns = @JoinColumn(name = "modulo_id"))
    @Builder.Default
    private Set<Modulo> modulos = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "plan_beneficio",
            schema = "public",
            joinColumns = @JoinColumn(name = "plan_id"),
            inverseJoinColumns = @JoinColumn(name = "beneficio_id"))
    @Builder.Default
    private Set<Beneficio> beneficios = new HashSet<>();

    @PrePersist
    void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.fechaCreacion = now;
        this.fechaActualizacion = now;
        if (this.estado == null) {
            this.estado = true;
        }
    }

    @PreUpdate
    void preUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }
}
