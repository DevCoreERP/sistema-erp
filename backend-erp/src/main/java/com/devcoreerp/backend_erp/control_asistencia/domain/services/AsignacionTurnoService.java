package com.devcoreerp.backend_erp.control_asistencia.domain.services;

import com.devcoreerp.backend_erp.control_asistencia.infrastructure.dto.AsignacionTurnoRequestDTO;
import com.devcoreerp.backend_erp.control_asistencia.infrastructure.dto.AsignacionTurnoResponseDTO;
import com.devcoreerp.backend_erp.control_asistencia.infrastructure.dto.AsignacionTurnoUpdateDTO;
import java.time.LocalDate;
import java.util.List;

public interface AsignacionTurnoService {

    AsignacionTurnoResponseDTO crearAsignacion(AsignacionTurnoRequestDTO dto);

    AsignacionTurnoResponseDTO actualizarAsignacion(Long id, AsignacionTurnoUpdateDTO dto);

    AsignacionTurnoResponseDTO obtenerPorId(Long id);

    List<AsignacionTurnoResponseDTO> obtenerTodos();

    List<AsignacionTurnoResponseDTO> obtenerPorUsuario(Long usuarioId);

    List<AsignacionTurnoResponseDTO> obtenerPorTurno(Long turnoId);

    List<AsignacionTurnoResponseDTO> obtenerActivas();

    AsignacionTurnoResponseDTO obtenerVigentePorUsuario(Long usuarioId);

    AsignacionTurnoResponseDTO cambiarEstadoAsignacion(Long id, Boolean estado);

    AsignacionTurnoResponseDTO finalizarAsignacion(Long id, LocalDate fechaFin);
}
