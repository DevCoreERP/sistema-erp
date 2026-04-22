package com.devcoreerp.backend_erp.organizational.infrastructure.persistence;

import com.devcoreerp.backend_erp.organizational.domain.Departamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.List;

@Repository
public interface DepartamentoRepository extends JpaRepository<Departamento, Long> {
    /**
     * Busca un deparatamento por su área
     */
    List<Departamento> findByAreaId(Long areaId);
    /**
     * Busca un departamento por su nombre
     */
    Optional<Departamento> findByNombre(String nombre);

    /**
     * Busca todas las departamento activas
     */
    Set<Departamento> findByActiveTrue();

    /**
     * Verifica si existe un departamento con el nombre dado
     */
    boolean existsByNombre(String nombre);

    /**
     * Busca departamento que contengan cierto texto en el nombre
     */
    List<Departamento> findByNombreContainingIgnoreCase(String nombre);

    @Query("SELECT a FROM Departamento a WHERE a.nombre = :nombre AND a.active = true")
    Optional<Departamento> findActiveByNombre(@Param("nombre") String nombre);
}