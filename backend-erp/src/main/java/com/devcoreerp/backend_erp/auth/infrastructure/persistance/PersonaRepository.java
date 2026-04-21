package com.devcoreerp.backend_erp.auth.infrastructure.persistance;

import com.devcoreerp.backend_erp.auth.domain.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * PersonaRepository: Acceso a datos para la entidad Persona
 */
@Repository
public interface PersonaRepository extends JpaRepository<Persona, Long> {
    
    /**
     * Busca una persona por email
     */
    Optional<Persona> findByEmail(String email);
    
    /**
     * Busca una persona por número de documento
     */
    Optional<Persona> findByDocumentNumber(String documentNumber);
    
    /**
     * Verifica si existe una persona con el email dado
     */
    boolean existsByEmail(String email);
    
    /**
     * Verifica si existe una persona con el documento dado
     */
    boolean existsByDocumentNumber(String documentNumber);
}