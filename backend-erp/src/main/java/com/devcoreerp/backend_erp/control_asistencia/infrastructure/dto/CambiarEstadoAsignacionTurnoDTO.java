package com.devcoreerp.backend_erp.control_asistencia.infrastructure.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CambiarEstadoAsignacionTurnoDTO {

    @NotNull(message = "El estado es obligatorio")
    private Boolean estado;
}
