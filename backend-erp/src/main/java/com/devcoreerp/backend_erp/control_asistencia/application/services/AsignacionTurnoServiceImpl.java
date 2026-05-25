package com.devcoreerp.backend_erp.control_asistencia.application.services;

import com.devcoreerp.backend_erp.auth.domain.Usuario;
import com.devcoreerp.backend_erp.auth.infrastructure.persistance.UsuarioRepository;
import com.devcoreerp.backend_erp.control_asistencia.application.mapper.AsignacionTurnoMapper;
import com.devcoreerp.backend_erp.control_asistencia.domain.AsignacionTurno;
import com.devcoreerp.backend_erp.control_asistencia.domain.Turno;
import com.devcoreerp.backend_erp.control_asistencia.domain.services.AsignacionTurnoService;
import com.devcoreerp.backend_erp.control_asistencia.infrastructure.dto.AsignacionTurnoRequestDTO;
import com.devcoreerp.backend_erp.control_asistencia.infrastructure.dto.AsignacionTurnoResponseDTO;
import com.devcoreerp.backend_erp.control_asistencia.infrastructure.dto.AsignacionTurnoUpdateDTO;
import com.devcoreerp.backend_erp.control_asistencia.infrastructure.persistance.AsignacionTurnoRepository;
import com.devcoreerp.backend_erp.control_asistencia.infrastructure.persistance.TurnoRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AsignacionTurnoServiceImpl implements AsignacionTurnoService {

    private static final LocalDate FECHA_MAXIMA = LocalDate.of(9999, 12, 31);

    private final AsignacionTurnoRepository asignacionTurnoRepository;
    private final UsuarioRepository usuarioRepository;
    private final TurnoRepository turnoRepository;
    private final AsignacionTurnoMapper asignacionTurnoMapper;

    public AsignacionTurnoServiceImpl(
            AsignacionTurnoRepository asignacionTurnoRepository,
            UsuarioRepository usuarioRepository,
            TurnoRepository turnoRepository,
            AsignacionTurnoMapper asignacionTurnoMapper) {
        this.asignacionTurnoRepository = asignacionTurnoRepository;
        this.usuarioRepository = usuarioRepository;
        this.turnoRepository = turnoRepository;
        this.asignacionTurnoMapper = asignacionTurnoMapper;
    }

    @Override
    @Transactional
    public AsignacionTurnoResponseDTO crearAsignacion(AsignacionTurnoRequestDTO dto) {
        validarRequestCreacion(dto);

        Usuario usuario = obtenerUsuario(dto.getUsuarioId());
        Turno turno = obtenerTurnoActivo(dto.getTurnoId());
        Boolean estado = dto.getEstado() != null ? dto.getEstado() : Boolean.TRUE;

        validarRangoFechas(dto.getFechaInicio(), dto.getFechaFin());
        validarSinSolapamiento(usuario.getId(), dto.getFechaInicio(), dto.getFechaFin(), null, estado);

        AsignacionTurno asignacion = AsignacionTurno.builder()
                .usuario(usuario)
                .turno(turno)
                .fechaInicio(dto.getFechaInicio())
                .fechaFin(dto.getFechaFin())
                .estado(estado)
                .build();

        return asignacionTurnoMapper.toResponseDTO(asignacionTurnoRepository.save(asignacion));
    }

    @Override
    @Transactional
    public AsignacionTurnoResponseDTO actualizarAsignacion(Long id, AsignacionTurnoUpdateDTO dto) {
        AsignacionTurno asignacion = obtenerAsignacion(id);

        Usuario usuario = dto.getUsuarioId() != null ? obtenerUsuario(dto.getUsuarioId()) : asignacion.getUsuario();
        Turno turno = dto.getTurnoId() != null ? obtenerTurnoActivo(dto.getTurnoId()) : asignacion.getTurno();
        LocalDate fechaInicio = dto.getFechaInicio() != null ? dto.getFechaInicio() : asignacion.getFechaInicio();
        LocalDate fechaFin = dto.getFechaFin() != null ? dto.getFechaFin() : asignacion.getFechaFin();
        Boolean estado = dto.getEstado() != null ? dto.getEstado() : asignacion.getEstado();

        if (Boolean.TRUE.equals(estado) && !Boolean.TRUE.equals(turno.getEstado())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se puede asignar un turno inactivo");
        }

        validarRangoFechas(fechaInicio, fechaFin);
        validarSinSolapamiento(usuario.getId(), fechaInicio, fechaFin, id, estado);

        asignacion.setUsuario(usuario);
        asignacion.setTurno(turno);
        asignacion.setFechaInicio(fechaInicio);
        asignacion.setFechaFin(fechaFin);
        asignacion.setEstado(estado);

        return asignacionTurnoMapper.toResponseDTO(asignacionTurnoRepository.save(asignacion));
    }

    @Override
    @Transactional(readOnly = true)
    public AsignacionTurnoResponseDTO obtenerPorId(Long id) {
        return asignacionTurnoMapper.toResponseDTO(obtenerAsignacion(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AsignacionTurnoResponseDTO> obtenerTodos() {
        return asignacionTurnoRepository.findAll().stream()
                .map(asignacionTurnoMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AsignacionTurnoResponseDTO> obtenerPorUsuario(Long usuarioId) {
        validarId(usuarioId, "El usuario es obligatorio");
        return asignacionTurnoRepository.findByUsuarioIdOrderByFechaInicioDesc(usuarioId).stream()
                .map(asignacionTurnoMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AsignacionTurnoResponseDTO> obtenerPorTurno(Long turnoId) {
        validarId(turnoId, "El turno es obligatorio");
        return asignacionTurnoRepository.findByTurnoIdOrderByFechaInicioDesc(turnoId).stream()
                .map(asignacionTurnoMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AsignacionTurnoResponseDTO> obtenerActivas() {
        return asignacionTurnoRepository.findByEstadoTrueOrderByFechaInicioDesc().stream()
                .map(asignacionTurnoMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AsignacionTurnoResponseDTO obtenerVigentePorUsuario(Long usuarioId) {
        validarId(usuarioId, "El usuario es obligatorio");
        return asignacionTurnoRepository.findVigentesByUsuarioId(usuarioId, LocalDate.now()).stream()
                .findFirst()
                .map(asignacionTurnoMapper::toResponseDTO)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No existe una asignacion de turno vigente para el usuario"));
    }

    @Override
    @Transactional
    public AsignacionTurnoResponseDTO cambiarEstadoAsignacion(Long id, Boolean estado) {
        if (estado == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El estado es obligatorio");
        }

        AsignacionTurno asignacion = obtenerAsignacion(id);
        if (Boolean.TRUE.equals(estado)) {
            if (!Boolean.TRUE.equals(asignacion.getTurno().getEstado())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se puede activar una asignacion con turno inactivo");
            }
            validarSinSolapamiento(
                    asignacion.getUsuario().getId(),
                    asignacion.getFechaInicio(),
                    asignacion.getFechaFin(),
                    id,
                    Boolean.TRUE);
        }

        asignacion.setEstado(estado);
        return asignacionTurnoMapper.toResponseDTO(asignacionTurnoRepository.save(asignacion));
    }

    @Override
    @Transactional
    public AsignacionTurnoResponseDTO finalizarAsignacion(Long id, LocalDate fechaFin) {
        if (fechaFin == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La fecha de fin es obligatoria");
        }

        AsignacionTurno asignacion = obtenerAsignacion(id);
        validarRangoFechas(asignacion.getFechaInicio(), fechaFin);
        asignacion.setFechaFin(fechaFin);
        asignacion.setEstado(false);

        return asignacionTurnoMapper.toResponseDTO(asignacionTurnoRepository.save(asignacion));
    }

    private void validarRequestCreacion(AsignacionTurnoRequestDTO dto) {
        if (dto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La asignacion de turno es obligatoria");
        }
        validarId(dto.getUsuarioId(), "El usuario es obligatorio");
        validarId(dto.getTurnoId(), "El turno es obligatorio");
        if (dto.getFechaInicio() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La fecha de inicio es obligatoria");
        }
    }

    private void validarId(Long id, String mensaje) {
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, mensaje);
        }
    }

    private void validarRangoFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        if (fechaInicio == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La fecha de inicio es obligatoria");
        }
        if (fechaFin != null && fechaFin.isBefore(fechaInicio)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La fecha de fin no puede ser anterior a la fecha de inicio");
        }
    }

    private Usuario obtenerUsuario(Long usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    private Turno obtenerTurnoActivo(Long turnoId) {
        Turno turno = turnoRepository.findById(turnoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Turno no encontrado"));
        if (!Boolean.TRUE.equals(turno.getEstado())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se puede asignar un turno inactivo");
        }
        return turno;
    }

    private AsignacionTurno obtenerAsignacion(Long id) {
        validarId(id, "La asignacion de turno es obligatoria");
        return asignacionTurnoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Asignacion de turno no encontrada"));
    }

    private void validarSinSolapamiento(
            Long usuarioId,
            LocalDate fechaInicio,
            LocalDate fechaFin,
            Long asignacionIdExcluir,
            Boolean estado) {
        if (!Boolean.TRUE.equals(estado)) {
            return;
        }

        LocalDate fechaFinEfectiva = fechaFin != null ? fechaFin : FECHA_MAXIMA;
        boolean existeSolapamiento = asignacionIdExcluir == null
                ? asignacionTurnoRepository.existsSolapamientoActivo(usuarioId, fechaInicio, fechaFinEfectiva, FECHA_MAXIMA)
                : asignacionTurnoRepository.existsSolapamientoActivoExcluyendoId(
                        usuarioId,
                        asignacionIdExcluir,
                        fechaInicio,
                        fechaFinEfectiva,
                        FECHA_MAXIMA);

        if (existeSolapamiento) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El usuario ya tiene una asignacion activa superpuesta en el periodo indicado");
        }
    }
}
