package com.devcoreerp.backend_erp.control_asistencia.application.mapper;

import com.devcoreerp.backend_erp.auth.domain.Usuario;
import com.devcoreerp.backend_erp.control_asistencia.domain.AsignacionTurno;
import com.devcoreerp.backend_erp.control_asistencia.domain.Turno;
import com.devcoreerp.backend_erp.control_asistencia.infrastructure.dto.AsignacionTurnoResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class AsignacionTurnoMapper {

    public AsignacionTurnoResponseDTO toResponseDTO(AsignacionTurno asignacion) {
        if (asignacion == null) {
            return null;
        }

        Usuario usuario = asignacion.getUsuario();
        Turno turno = asignacion.getTurno();

        return AsignacionTurnoResponseDTO.builder()
                .id(asignacion.getId())
                .usuarioId(usuario != null ? usuario.getId() : null)
                .username(usuario != null ? usuario.getUsername() : null)
                .nombreUsuario(usuario != null ? usuario.getFullName() : null)
                .turnoId(turno != null ? turno.getId() : null)
                .nombreTurno(turno != null ? turno.getNombre() : null)
                .fechaInicio(asignacion.getFechaInicio())
                .fechaFin(asignacion.getFechaFin())
                .estado(asignacion.getEstado())
                .build();
    }
}
