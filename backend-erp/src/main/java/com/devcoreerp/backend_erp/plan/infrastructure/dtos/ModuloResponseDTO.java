package com.devcoreerp.backend_erp.plan.infrastructure.dtos;

public record ModuloResponseDTO(
        Long id,
        String nombre,
        String descripcion,
        Boolean estado
) {
}
