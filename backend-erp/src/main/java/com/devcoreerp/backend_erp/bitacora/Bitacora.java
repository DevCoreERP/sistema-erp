package com.devcoreerp.backend_erp.bitacora;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bitacora", schema = "public")
public class Bitacora {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ip", nullable = false, updatable = false)
    private String ip;

    @Column(name ="usuario", nullable = true, updatable = false)
    private String usuario;

    @Column(name = "tenant", nullable = true, updatable = false)
    private String tenant;

    @Column(name = "endpoint", nullable = false, updatable = false)
    private String endpoint;

    @Column(name = "http_status", nullable = false, updatable = false)
    private Integer httpStatus;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }

    // Getters and Setters
}