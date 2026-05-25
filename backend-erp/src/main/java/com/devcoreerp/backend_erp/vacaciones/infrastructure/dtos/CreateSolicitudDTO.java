package com.devcoreerp.backend_erp.vacaciones.infrastructure.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.AssertTrue;

import java.util.Date;

public record CreateSolicitudDTO(
    String estado,

    @NotNull(message = "La fechaInicio no puede estar vacío")
    @FutureOrPresent
    Date fechaInicio,

    @NotNull(message = "La fechaFin no puede estar vacío")
    @FutureOrPresent
    Date fechaFin,

    @NotNull(message = "El vacacionId es obligatorio")
    Long vacacionId
) {
    @AssertTrue(message = "La fechaFin debe ser posterior o igual a la fechaInicio")
    public boolean isFechaRangoValido() {
        if (fechaInicio == null || fechaFin == null) {
            return true; // @NotNull
        }
        return !fechaFin.before(fechaInicio);
    }

    @AssertTrue(message = "El estado debe ser [pendiente|aprobado|rechazado]")
    public boolean isEstadoValid() {
        return estado == "aprobado" || estado == "pendiente" || estado == "rechazado";
    }
}
