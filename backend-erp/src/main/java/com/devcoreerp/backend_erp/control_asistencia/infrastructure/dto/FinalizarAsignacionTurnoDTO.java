package com.devcoreerp.backend_erp.control_asistencia.infrastructure.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
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
public class FinalizarAsignacionTurnoDTO {

    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDate fechaFin;
}
