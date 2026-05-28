package com.devcoreerp.backend_erp.control_asistencia.infrastructure.dto;

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
public class AsignacionTurnoUpdateDTO {

    private Long usuarioId;
    private Long turnoId;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Boolean estado;
}
