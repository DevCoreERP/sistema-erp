package com.devcoreerp.backend_erp.pago.infrastructure.dtos;

import com.devcoreerp.backend_erp.pago.domain.PagoEstado;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PagoResponseDTO(
        Long id,
        Long suscripcionId,
        Long tenantMetodoPagoId,
        String metodoPagoNombre,
        PagoEstado estado,
        BigDecimal montoPagadoUsd,
        LocalDateTime fechaPago,
        String codigoPago,
        String observacion,
        LocalDateTime fechaCreacion
) {
}
