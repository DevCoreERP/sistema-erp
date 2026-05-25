package com.devcoreerp.backend_erp.control_asistencia.infrastructure.persistance;

import com.devcoreerp.backend_erp.control_asistencia.domain.AsignacionTurno;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AsignacionTurnoRepository extends JpaRepository<AsignacionTurno, Long> {

    @Override
    @EntityGraph(attributePaths = {"usuario", "turno"})
    List<AsignacionTurno> findAll();

    @EntityGraph(attributePaths = {"usuario", "turno"})
    List<AsignacionTurno> findByUsuarioIdOrderByFechaInicioDesc(Long usuarioId);

    @EntityGraph(attributePaths = {"usuario", "turno"})
    List<AsignacionTurno> findByTurnoIdOrderByFechaInicioDesc(Long turnoId);

    @EntityGraph(attributePaths = {"usuario", "turno"})
    List<AsignacionTurno> findByEstadoTrueOrderByFechaInicioDesc();

    @Query("""
            select a from AsignacionTurno a
            join fetch a.usuario
            join fetch a.turno
            where a.usuario.id = :usuarioId
              and a.estado = true
              and a.fechaInicio <= :fecha
              and (a.fechaFin is null or a.fechaFin >= :fecha)
            order by a.fechaInicio desc
            """)
    List<AsignacionTurno> findVigentesByUsuarioId(
            @Param("usuarioId") Long usuarioId,
            @Param("fecha") LocalDate fecha);

    @Query("""
            select a from AsignacionTurno a
            join fetch a.usuario
            join fetch a.turno t
            where a.usuario.id = :usuarioId
              and a.estado = true
              and t.estado = true
              and a.fechaInicio <= :hasta
              and coalesce(a.fechaFin, :fechaMaxima) >= :desde
            order by a.fechaInicio asc, t.horaInicio asc
            """)
    List<AsignacionTurno> findAgendaActivaByUsuarioAndPeriodo(
            @Param("usuarioId") Long usuarioId,
            @Param("desde") LocalDate desde,
            @Param("hasta") LocalDate hasta,
            @Param("fechaMaxima") LocalDate fechaMaxima);

    @Query("""
            select count(a) > 0 from AsignacionTurno a
            where a.usuario.id = :usuarioId
              and a.estado = true
              and a.fechaInicio <= :fechaFinNueva
              and :fechaInicioNueva <= coalesce(a.fechaFin, :fechaMaxima)
            """)
    boolean existsSolapamientoActivo(
            @Param("usuarioId") Long usuarioId,
            @Param("fechaInicioNueva") LocalDate fechaInicioNueva,
            @Param("fechaFinNueva") LocalDate fechaFinNueva,
            @Param("fechaMaxima") LocalDate fechaMaxima);

    @Query("""
            select count(a) > 0 from AsignacionTurno a
            where a.usuario.id = :usuarioId
              and a.estado = true
              and a.id <> :asignacionId
              and a.fechaInicio <= :fechaFinNueva
              and :fechaInicioNueva <= coalesce(a.fechaFin, :fechaMaxima)
            """)
    boolean existsSolapamientoActivoExcluyendoId(
            @Param("usuarioId") Long usuarioId,
            @Param("asignacionId") Long asignacionId,
            @Param("fechaInicioNueva") LocalDate fechaInicioNueva,
            @Param("fechaFinNueva") LocalDate fechaFinNueva,
            @Param("fechaMaxima") LocalDate fechaMaxima);
}
