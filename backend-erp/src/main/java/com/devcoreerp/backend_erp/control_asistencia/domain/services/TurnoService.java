package com.devcoreerp.backend_erp.control_asistencia.domain.services;

import java.util.List;

import com.devcoreerp.backend_erp.control_asistencia.infrastructure.dto.TurnoRequestDTO;
import com.devcoreerp.backend_erp.control_asistencia.infrastructure.dto.TurnoResponseDTO;

public interface TurnoService {
    TurnoResponseDTO crearTurno(TurnoRequestDTO dto);
    TurnoResponseDTO actualizarTurno(Long id, TurnoRequestDTO dto);
    TurnoResponseDTO obtenerPorId(Long id);
    List<TurnoResponseDTO> obtenerTodos();
    TurnoResponseDTO cambiarEstadoTurno(Long id, Boolean estado);
}
