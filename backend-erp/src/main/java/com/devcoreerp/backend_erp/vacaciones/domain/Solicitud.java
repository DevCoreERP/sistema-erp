package com.devcoreerp.backend_erp.vacaciones.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
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
import java.time.LocalDate;

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
    private LocalDate fechaInicio;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false, updatable = true)
    private LocalDate fechaFin;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = true)
    private Date updatedAt;

    @Column(name = "saldo_id", nullable = false)
    private Long saldo;

    public Solicitud(Long saldo, LocalDate fechaInicio, LocalDate fechaFin) {
        this.saldo = saldo;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.estado = "pendiente";
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

}
