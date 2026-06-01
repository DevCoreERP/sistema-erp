package com.devcoreerp.backend_erp.plan.infrastructure.dtos;

public record BeneficioResponseDTO(
        Long id,
        String nombre,
        String descripcion,
        Boolean estado
) {
}
