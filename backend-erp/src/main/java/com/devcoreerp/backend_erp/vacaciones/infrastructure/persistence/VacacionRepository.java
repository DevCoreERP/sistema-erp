package com.devcoreerp.backend_erp.vacaciones.infrastructure.persistence;

import com.devcoreerp.backend_erp.vacaciones.domain.Vacacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface VacacionRepository extends JpaRepository<Vacacion, Long> {
    /**
     * Busca una vacacion por empleadoId
     */
    Optional<Vacacion> findByEmpleadoId(Long empleadoId);

    /**
     * Busca una vacacion por numero de dias
     */
    List<Vacacion> findByDias(Long dias);
}