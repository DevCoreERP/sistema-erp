package com.devcoreerp.backend_erp.vacaciones.infrastructure.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record CreateSaldoDTO(
    @NotNull(message = "Los dias no puede estar vacio")
    @Max(value = 365, message = "Maximo 365 dias")
    @Min(value = 0, message = "Minino 0 dias")
    Long dias,

    @NotNull(message = "El usuarioId es obligatorio")
    Long usuarioId
) {}
