package com.devcoreerp.backend_erp.organizational.infrastructure.persistence;

import com.devcoreerp.backend_erp.organizational.domain.Area;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.List;

@Repository
public interface AreaRepository extends JpaRepository<Area, Long> {
    /**
     * Busca un área por su nombre
     */
    Optional<Area> findByNombre(String nombre);

    /**
     * Busca todas las áreas activas
     */
    Set<Area> findByActiveTrue();

    /**
     * Verifica si existe un área con el nombre dado
     */
    boolean existsByNombre(String nombre);

    /**
     * Busca áreas que contengan cierto texto en el nombre
     */
    List<Area> findByNombreContainingIgnoreCase(String nombre);

    @Query("SELECT a FROM Area a WHERE a.nombre = :nombre AND a.active = true")
    Optional<Area> findActiveByNombre(@Param("nombre") String nombre);
}