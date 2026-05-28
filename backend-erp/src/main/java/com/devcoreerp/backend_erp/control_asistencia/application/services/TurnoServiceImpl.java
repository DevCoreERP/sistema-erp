package com.devcoreerp.backend_erp.control_asistencia.application.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.devcoreerp.backend_erp.control_asistencia.application.mapper.TurnoMapper;
import com.devcoreerp.backend_erp.control_asistencia.domain.Turno;
import com.devcoreerp.backend_erp.control_asistencia.domain.services.TurnoService;
import com.devcoreerp.backend_erp.control_asistencia.infrastructure.dto.TurnoRequestDTO;
import com.devcoreerp.backend_erp.control_asistencia.infrastructure.dto.TurnoResponseDTO;
import com.devcoreerp.backend_erp.control_asistencia.infrastructure.persistance.TurnoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TurnoServiceImpl implements TurnoService {

    private final TurnoRepository turnoRepository;
    private final TurnoMapper mapperTurno;

    @Override
    @Transactional
    public TurnoResponseDTO crearTurno(TurnoRequestDTO dto) {
        // Validar nombre único
        if (dto.getNombre() != null) {
            if (turnoRepository.existsByNombre(dto.getNombre())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "El telefono ya está en uso");
            }
        }
        Turno turno = mapperTurno.toEntity(dto);
        turno.setEstado(true); // Se registra activo por defecto
        return mapperTurno.toResponseDTO(turno);
    }

    @Override
    @Transactional
    public TurnoResponseDTO actualizarTurno(Long id, TurnoRequestDTO dto) {
        Turno turno = turnoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Turno no encontrado con el ID: " + id));
        
        mapperTurno.updateFromDTO(dto, turno);
        return mapperTurno.toResponseDTO(turno);
    }

    @Override
    @Transactional(readOnly = true)
    public TurnoResponseDTO obtenerPorId(Long id) {
        return turnoRepository.findById(id)
                .map(mapperTurno::toResponseDTO)
                .orElseThrow(() -> new RuntimeException("Turno no encontrado con el ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TurnoResponseDTO> obtenerTodos() {
        return turnoRepository.findAll().stream()
                .map(mapperTurno::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TurnoResponseDTO cambiarEstadoTurno(Long id, Boolean estado) {
        Turno turno = turnoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Turno no encontrado con el ID: " + id));
        
        turno.setEstado(estado);
        return mapperTurno.toResponseDTO(turno);
    }
}