package com.devcoreerp.backend_erp.vacaciones.domain;

import com.devcoreerp.backend_erp.empleados.domain.Empleado;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "vacaciones")
public class Vacacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long dias;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = true)
    private Date updatedAt;

    // Relacion 1:1 con Empleado
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "empleado_id", unique = true, nullable = false)
    private Empleado empleado;

    // Relación 1:M con Solicitud
    @OneToMany(mappedBy = "solicitud")
    @JsonIgnore
    private List<Solicitud> solicitudes;

    public Vacacion(Empleado empleado, Long dias) {
        this.empleado = empleado;
        this.dias = dias;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

}
