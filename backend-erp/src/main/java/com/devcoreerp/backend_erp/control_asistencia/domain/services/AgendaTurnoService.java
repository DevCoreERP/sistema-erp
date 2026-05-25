package com.devcoreerp.backend_erp.control_asistencia.domain.services;

import com.devcoreerp.backend_erp.control_asistencia.infrastructure.dto.AgendaTurnoResponseDTO;
import java.util.List;

public interface AgendaTurnoService {

    List<AgendaTurnoResponseDTO> consultarMiAgenda(Long usuarioId, Integer dias);
}
