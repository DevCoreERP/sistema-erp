package com.devcoreerp.backend_erp.pago.infrastructure.dtos;

import java.time.LocalDateTime;

public record TenantMetodoPagoResponseDTO(
        Long id,
        Long metodoPagoId,
        String metodoPagoNombre,
        String titular,
        String ultimosDigitos,
        String marca,
        String referencia,
        Boolean esPrincipal,
        Boolean estado,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaActualizacion
) {
}
