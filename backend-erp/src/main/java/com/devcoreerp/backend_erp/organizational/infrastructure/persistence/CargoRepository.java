package com.devcoreerp.backend_erp.organizational.infrastructure.persistence;

import com.devcoreerp.backend_erp.organizational.domain.Cargo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.List;

@Repository
public interface CargoRepository extends JpaRepository<Cargo, Long> {
    /**
     * Busca un cargo por su departamento
     */
    List<Cargo> findByDepartamentoId(Long departamentoId);
    /**
     * Busca un cargo por su nombre
     */
    Optional<Cargo> findByNombre(String nombre);

    /**
     * Busca todas las cargo activas
     */
    Set<Cargo> findByActiveTrue();

    /**
     * Verifica si existe un cargo con el nombre dado
     */
    boolean existsByNombre(String nombre);

    /**
     * Busca cargo que contengan cierto texto en el nombre
     */
    List<Cargo> findByNombreContainingIgnoreCase(String nombre);

    @Query("SELECT a FROM Cargo a WHERE a.nombre = :nombre AND a.active = true")
    Optional<Cargo> findActiveByNombre(@Param("nombre") String nombre);
}
