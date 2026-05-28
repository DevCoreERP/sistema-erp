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
public class AsignacionTurnoResponseDTO {

    private Long id;
    private Long usuarioId;
    private String username;
    private String nombreUsuario;
    private Long turnoId;
    private String nombreTurno;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Boolean estado;
}
