package com.devcoreerp.backend_erp.empleados.domain;

import com.devcoreerp.backend_erp.auth.domain.Persona;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

/**
 * Entidad Empleado: Representa a un empleado de la organización.
 * 
 * Relación:
 * - 1:1 con Persona (un empleado está vinculado a una persona única)
 * - Una Persona puede tener solo un Empleado activo
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "empleados")
public class Empleado {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Relación 1:1 con Persona
     * Un empleado está vinculado a una única persona
     */
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "persona_id", unique = true, nullable = false)
    private Persona persona;
    
    @Column(name = "codigo_empleado", unique = true, nullable = false, length = 50)
    private String codigoEmpleado; // Código único del empleado (ej: EMP-001)
    
    @Temporal(TemporalType.DATE)
    @Column(name = "fecha_ingreso", nullable = false)
    private Date fechaIngreso; // Fecha de ingreso del empleado
    
    @Column(nullable = false)
    private Boolean estado = true; // true = activo, false = inactivo
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date createdAt;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
        if (fechaIngreso == null) {
            fechaIngreso = new Date();
        }
        if (estado == null) {
            estado = true;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }
    
    /**
     * Constructor de conveniencia
     */
    public Empleado(Persona persona, String codigoEmpleado, Date fechaIngreso) {
        this.persona = persona;
        this.codigoEmpleado = codigoEmpleado;
        this.fechaIngreso = fechaIngreso;
        this.estado = true;
        this.createdAt = new Date();
    }
    
    /**
     * Obtiene el nombre completo del empleado desde su Persona
     */
    public String getFullName() {
        return persona != null ? persona.getFullName() : "N/A";
    }
    
    /**
     * Obtiene el email del empleado desde su Persona
     */
    public String getEmail() {
        return persona != null ? persona.getEmail() : null;
    }
}