package com.devcoreerp.backend_erp.subcripcion.domain;

import com.devcoreerp.backend_erp.multitenancy.Tenant;
import com.devcoreerp.backend_erp.pago.domain.TenantMetodoPago;
import com.devcoreerp.backend_erp.plan.domain.Plan;
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
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
@Table(name = "suscripcion", schema = "public")
public class Suscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_metodo_pago_id")
    private TenantMetodoPago tenantMetodoPago;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SuscripcionEstado estado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SuscripcionTipo tipo;

    @Column(name = "fecha_proximo_vencimiento")
    private LocalDate fechaProximoVencimiento;

    @Column(name = "plan_nombre_snapshot", nullable = false, length = 120)
    private String planNombreSnapshot;

    @Column(name = "precio_usd_snapshot", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUsdSnapshot;

    @Column(name = "limite_usuarios_snapshot", nullable = false)
    private Integer limiteUsuariosSnapshot;

    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    public boolean estaVigente(LocalDate fecha) {
        return fecha != null
                && (fecha.isEqual(fechaInicio) || fecha.isAfter(fechaInicio))
                && (fecha.isEqual(fechaFin) || fecha.isBefore(fechaFin));
    }

    @PrePersist
    void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.fechaCreacion = now;
        this.fechaActualizacion = now;
    }

    @PreUpdate
    void preUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }
}
