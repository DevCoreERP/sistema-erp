package com.devcoreerp.backend_erp.subcripcion.infrastructure.dtos;

import com.devcoreerp.backend_erp.subcripcion.domain.SuscripcionEstado;
import com.devcoreerp.backend_erp.subcripcion.domain.SuscripcionTipo;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record SuscripcionResponseDTO(
        Long id,
        Long tenantId,
        Long planId,
        String planNombre,
        Long tenantMetodoPagoId,
        SuscripcionEstado estado,
        SuscripcionTipo tipo,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        LocalDate fechaProximoVencimiento,
        String planNombreSnapshot,
        BigDecimal precioUsdSnapshot,
        Integer limiteUsuariosSnapshot,
        Boolean vigente,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaActualizacion
) {
}
