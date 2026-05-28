package com.devcoreerp.backend_erp.vacaciones.infrastructure.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.AssertTrue;

import java.time.LocalDate;

public record CreateSolicitudDTO(
    String estado,

    @NotNull(message = "La fechaInicio no puede estar vacío")
    @FutureOrPresent
    LocalDate fechaInicio,

    @NotNull(message = "La fechaFin no puede estar vacío")
    @FutureOrPresent
    LocalDate fechaFin,

    @NotNull(message = "El saldoId es obligatorio")
    Long saldoId
) {
    @AssertTrue(message = "La fechaFin debe ser posterior o igual a la fechaInicio")
    public boolean isFechaRangoValido() {
        if (fechaInicio == null || fechaFin == null) {
            return true; // @NotNull
        }
        return !fechaFin.isBefore(fechaInicio());
    }

    @AssertTrue(message = "El estado debe ser [pendiente|aprobado|rechazado]")
    public boolean isEstadoValid() {
        return estado == "aprobado" || estado == "pendiente" || estado == "rechazado";
    }
}
