package com.devcoreerp.backend_erp.subcripcion.infrastructure.dtos;

import jakarta.validation.constraints.NotNull;

public record SuscripcionCompraRequestDTO(
        @NotNull Long planId,
        @NotNull Long tenantMetodoPagoId
) {
}
