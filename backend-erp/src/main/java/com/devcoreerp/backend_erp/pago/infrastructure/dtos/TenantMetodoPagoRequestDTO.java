package com.devcoreerp.backend_erp.pago.infrastructure.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TenantMetodoPagoRequestDTO(
        @NotNull Long metodoPagoId,
        @NotBlank @Size(max = 150) String titular,
        @Size(max = 64) String ultimosDigitos,
        @Size(max = 80) String marca,
        @Size(max = 150) String referencia,
        Boolean esPrincipal,
        Boolean estado
) {
}
