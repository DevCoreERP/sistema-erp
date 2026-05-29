package com.devcoreerp.backend_erp.control_asistencia.infrastructure.dto;

import java.time.LocalDate;
import java.time.LocalTime;
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
public class AgendaTurnoResponseDTO {

    private LocalDate fecha;
    private Long turnoId;
    private String nombreTurno;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private Boolean horarioActivo;
    private Long asignacionId;
}
