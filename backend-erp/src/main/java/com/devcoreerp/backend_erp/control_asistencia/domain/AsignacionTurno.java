package com.devcoreerp.backend_erp.control_asistencia.domain;

import com.devcoreerp.backend_erp.auth.domain.Usuario;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "asignaciones_turno", indexes = {
        @Index(name = "idx_asignaciones_turno_usuario", columnList = "usuario_id"),
        @Index(name = "idx_asignaciones_turno_turno", columnList = "turno_id"),
        @Index(name = "idx_asignaciones_turno_estado", columnList = "estado"),
        @Index(name = "idx_asignaciones_turno_fecha_i", columnList = "fecha_i"),
        @Index(name = "idx_asignaciones_turno_fecha_f", columnList = "fecha_f"),
        @Index(name = "idx_asignaciones_turno_usuario_periodo", columnList = "usuario_id, estado, fecha_i, fecha_f")
})
public class AsignacionTurno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "turno_id", nullable = false)
    private Turno turno;

    @Column(name = "fecha_i", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_f")
    private LocalDate fechaFin;

    @Column(nullable = false)
    @Builder.Default
    private Boolean estado = true;
}
