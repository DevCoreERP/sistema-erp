package com.devcoreerp.backend_erp.vacaciones.application.mappers;

import com.devcoreerp.backend_erp.vacaciones.infrastructure.dtos.CreateSolicitudDTO;
import com.devcoreerp.backend_erp.vacaciones.infrastructure.dtos.ResponseSolicitudDTO;
import com.devcoreerp.backend_erp.vacaciones.domain.Solicitud;
import com.devcoreerp.backend_erp.vacaciones.domain.Vacacion;

public class SolicitudMapper {

    public static ResponseSolicitudDTO toDTO(Solicitud solicitud) {
        if (solicitud == null) return null;

        Vacacion vacacion = solicitud.getVacacion();

        return new ResponseSolicitudDTO(
            solicitud.getId(),
            solicitud.getEstado(),
            solicitud.getFechaInicio(),
            solicitud.getFechaFin(),
            solicitud.getCreatedAt(),
            solicitud.getUpdatedAt(),
            VacacionMapper.toDTO(vacacion)
        );
    }
    public static Solicitud toEntity(CreateSolicitudDTO dto, Vacacion vacacion) {
        if (dto == null) return null;
        Solicitud solicitud = new Solicitud(vacacion, dto.fechaInicio(), dto.fechaFin());
        return solicitud;
    }
}