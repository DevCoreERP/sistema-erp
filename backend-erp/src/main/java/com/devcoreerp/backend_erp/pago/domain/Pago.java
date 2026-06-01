package com.devcoreerp.backend_erp.pago.domain;

import com.devcoreerp.backend_erp.multitenancy.Tenant;
import com.devcoreerp.backend_erp.subcripcion.domain.Suscripcion;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
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
@Table(name = "pago", schema = "public")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "suscripcion_id", nullable = false)
    private Suscripcion suscripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_metodo_pago_id", nullable = false)
    private TenantMetodoPago tenantMetodoPago;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PagoEstado estado;

    @Column(name = "monto_pagado_usd", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoPagadoUsd;

    @Column(name = "fecha_pago", nullable = false)
    private LocalDateTime fechaPago;

    @Column(name = "codigo_pago", nullable = false, unique = true, length = 80)
    private String codigoPago;

    @Column(length = 255)
    private String observacion;

    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @PrePersist
    void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.fechaCreacion = now;
        if (this.fechaPago == null) {
            this.fechaPago = now;
        }
        if (this.estado == null) {
            this.estado = PagoEstado.PAGADO;
        }
        if (this.codigoPago == null || this.codigoPago.isBlank()) {
            this.codigoPago = "PAY-" + UUID.randomUUID();
        }
    }
}
