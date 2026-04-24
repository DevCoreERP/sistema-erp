package com.devcoreerp.backend_erp.vacaciones.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "solicitudes")
public class Solicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = true)
    private String estado;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false, updatable = true)
    private Date fechaInicio;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false, updatable = true)
    private Date fechaFin;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = true)
    private Date updatedAt;

    // Relación M:1 con Vacacion
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vacacion_id", nullable = false)
    private Vacacion vacacion;

    public Solicitud(Vacacion vacacion, Date fechaInicio, Date fechaFin) {
        this.vacacion = vacacion;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.estado = "pendiente";
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

}
