package com.devcoreerp.backend_erp.auth.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "permissions")
public class Permission {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false, length = 100)
    private String code; // ej: PRODUCT_CREATE, USER_DELETE, REPORT_VIEW
    
    @Column(nullable = false, length = 255)
    private String description; // descripción legible del permiso
    
    @Column(nullable = false)
    private String category; // ej: PRODUCT, USER, REPORT
    
    public Permission(String code, String description, String category) {
        this.code = code;
        this.description = description;
        this.category = category;
    }
}