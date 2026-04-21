package com.devcoreerp.backend_erp.auth.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.OneToOne;
import jakarta.persistence.FetchType;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.Date;

/**
 * Entidad Persona: Contiene datos civiles y personales del usuario.
 * Separada de Usuario para mantener una arquitectura limpia.
 * 
 * Relación 1:1 con Usuario
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "personas")
public class Persona {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String firstName;
    
    @Column(nullable = false, length = 100)
    private String surnames;
    
    @Column(unique = true, nullable = false, length = 50)
    private String documentType; // ej: CI, PASSPORT
    
    @Column(unique = true, nullable = false, length = 50)
    private String documentNumber;
    
    @Column(unique = true, nullable = false, length = 150)
    private String email;
    
    @Column(length = 20)
    private String phoneNumber;
    
    @Column(length = 255)
    private String address;
    
    @Temporal(TemporalType.DATE)
    private Date birthDate;
    
    @Column(nullable = false)
    private Boolean active = true;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date createdAt;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
    
    // Relación 1:1 con Usuario (relación inversa en Usuario)
    @OneToOne(mappedBy = "persona", fetch = FetchType.LAZY)
    private Usuario usuario;
    
    public Persona(String firstName, String surnames, String email, String phoneNumber) {
        this.firstName = firstName;
        this.surnames = surnames;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.active = true;
        this.createdAt = new Date();
    }
    
    public String getFullName() {
        return firstName + " " + surnames;
    }
}