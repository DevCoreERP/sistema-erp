package com.devcoreerp.backend_erp.vacaciones.application.mappers;

import com.devcoreerp.backend_erp.vacaciones.infrastructure.dtos.CreateSolicitudDTO;
import com.devcoreerp.backend_erp.vacaciones.infrastructure.dtos.ResponseSolicitudDTO;
import com.devcoreerp.backend_erp.vacaciones.domain.Solicitud;

public class SolicitudMapper {

    public static ResponseSolicitudDTO toDTO(Solicitud solicitud) {
        if (solicitud == null) return null;

        return new ResponseSolicitudDTO(
            solicitud.getId(),
            solicitud.getEstado(),
            solicitud.getFechaInicio(),
            solicitud.getFechaFin(),
            solicitud.getCreatedAt(),
            solicitud.getUpdatedAt(),
            solicitud.getSaldo()
        );
    }
    public static Solicitud toEntity(CreateSolicitudDTO dto, Long saldo) {
        if (dto == null) return null;
        Solicitud solicitud = new Solicitud(saldo, dto.fechaInicio(), dto.fechaFin());
        return solicitud;
    }
}