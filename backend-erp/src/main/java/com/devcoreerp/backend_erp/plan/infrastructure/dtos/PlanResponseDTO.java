package com.devcoreerp.backend_erp.plan.infrastructure.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

public record PlanResponseDTO(
        Long id,
        String nombre,
        String descripcion,
        BigDecimal precioUsd,
        Integer limiteUsuarios,
        Boolean estado,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaActualizacion,
        Set<ModuloResponseDTO> modulos,
        Set<BeneficioResponseDTO> beneficios
) {
}
