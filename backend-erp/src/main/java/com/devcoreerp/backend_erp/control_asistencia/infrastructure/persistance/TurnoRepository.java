package com.devcoreerp.backend_erp.control_asistencia.infrastructure.persistance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devcoreerp.backend_erp.control_asistencia.domain.Turno;

@Repository
public interface TurnoRepository extends JpaRepository<Turno, Long> {

    boolean existsByNombre(String nombre);

}