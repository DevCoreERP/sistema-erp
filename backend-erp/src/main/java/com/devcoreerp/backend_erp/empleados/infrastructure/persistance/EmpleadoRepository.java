package com.devcoreerp.backend_erp.empleados.infrastructure.persistance;

import com.devcoreerp.backend_erp.empleados.domain.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la entidad Empleado
 */
@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {
    
    /**
     * Busca un empleado por código de empleado
     */
    Optional<Empleado> findByCodigoEmpleado(String codigoEmpleado);
    
    /**
     * Verifica si existe un empleado con el código especificado
     */
    boolean existsByCodigoEmpleado(String codigoEmpleado);
    
    /**
     * Busca un empleado activo por persona ID
     */
    @Query("SELECT e FROM Empleado e WHERE e.persona.id = :personaId AND e.estado = true")
    Optional<Empleado> findActiveByPersonaId(@Param("personaId") Long personaId);
    
    /**
     * Verifica si existe un empleado activo para una persona
     */
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END " +
           "FROM Empleado e WHERE e.persona.id = :personaId AND e.estado = true")
    boolean existsActiveByPersonaId(@Param("personaId") Long personaId);
}