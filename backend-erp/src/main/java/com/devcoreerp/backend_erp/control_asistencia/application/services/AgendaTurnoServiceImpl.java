package com.devcoreerp.backend_erp.control_asistencia.application.services;

import com.devcoreerp.backend_erp.control_asistencia.domain.AsignacionTurno;
import com.devcoreerp.backend_erp.control_asistencia.domain.Turno;
import com.devcoreerp.backend_erp.control_asistencia.domain.services.AgendaTurnoService;
import com.devcoreerp.backend_erp.control_asistencia.infrastructure.dto.AgendaTurnoResponseDTO;
import com.devcoreerp.backend_erp.control_asistencia.infrastructure.persistance.AsignacionTurnoRepository;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AgendaTurnoServiceImpl implements AgendaTurnoService {

    private static final int DIAS_DEFAULT = 7;
    private static final int DIAS_MINIMO = 1;
    private static final int DIAS_MAXIMO = 31;
    private static final LocalDate FECHA_MAXIMA = LocalDate.of(9999, 12, 31);

    private final AsignacionTurnoRepository asignacionTurnoRepository;

    public AgendaTurnoServiceImpl(AsignacionTurnoRepository asignacionTurnoRepository) {
        this.asignacionTurnoRepository = asignacionTurnoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AgendaTurnoResponseDTO> consultarMiAgenda(Long usuarioId, Integer dias) {
        if (usuarioId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El usuario es obligatorio");
        }

        int diasConsulta = normalizarDias(dias);
        LocalDate desde = LocalDate.now();
        LocalDate hasta = desde.plusDays(diasConsulta - 1L);

        return asignacionTurnoRepository
                .findAgendaActivaByUsuarioAndPeriodo(usuarioId, desde, hasta, FECHA_MAXIMA)
                .stream()
                .flatMap(asignacion -> expandirAsignacion(asignacion, desde, hasta))
                .sorted(Comparator.comparing(AgendaTurnoResponseDTO::getFecha)
                        .thenComparing(AgendaTurnoResponseDTO::getHoraInicio))
                .toList();
    }

    private int normalizarDias(Integer dias) {
        int diasConsulta = dias != null ? dias : DIAS_DEFAULT;
        if (diasConsulta < DIAS_MINIMO || diasConsulta > DIAS_MAXIMO) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El rango de dias debe estar entre 1 y 31");
        }
        return diasConsulta;
    }

    private Stream<AgendaTurnoResponseDTO> expandirAsignacion(
            AsignacionTurno asignacion,
            LocalDate desde,
            LocalDate hasta) {
        LocalDate inicio = asignacion.getFechaInicio().isBefore(desde) ? desde : asignacion.getFechaInicio();
        LocalDate finAsignacion = asignacion.getFechaFin() != null ? asignacion.getFechaFin() : hasta;
        LocalDate fin = finAsignacion.isAfter(hasta) ? hasta : finAsignacion;

        return inicio.datesUntil(fin.plusDays(1))
                .map(fecha -> toAgendaDTO(asignacion, fecha));
    }

    private AgendaTurnoResponseDTO toAgendaDTO(AsignacionTurno asignacion, LocalDate fecha) {
        Turno turno = asignacion.getTurno();
        return AgendaTurnoResponseDTO.builder()
                .fecha(fecha)
                .turnoId(turno.getId())
                .nombreTurno(turno.getNombre())
                .horaInicio(turno.getHoraInicio())
                .horaFin(turno.getHoraFin())
                .horarioActivo(Boolean.TRUE.equals(asignacion.getEstado()) && Boolean.TRUE.equals(turno.getEstado()))
                .asignacionId(asignacion.getId())
                .build();
    }
}
