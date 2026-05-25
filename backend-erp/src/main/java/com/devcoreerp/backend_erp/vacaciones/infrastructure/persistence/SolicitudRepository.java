package com.devcoreerp.backend_erp.vacaciones.infrastructure.persistence;

import com.devcoreerp.backend_erp.vacaciones.domain.Solicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Date;

@Repository
public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {
    /**
     * Busca un deparatamento por su vacacion
     */
    List<Solicitud> findByVacacionId(Long areaId);

    /**
     * Busca un departamento por su fechaInicio
     */
    List<Solicitud> findByFechaInicio(Date fechaInicio);

    /**
     * Busca un departamento por su fechaFin
     */
    List<Solicitud> findByFechaFin(Date fechaFin);

    /**
     * Busca un departamento por su estado
     */
    List<Solicitud> findByEstado(String estado);
}