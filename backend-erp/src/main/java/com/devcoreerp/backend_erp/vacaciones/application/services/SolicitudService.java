package com.devcoreerp.backend_erp.vacaciones.application.services;

import com.devcoreerp.backend_erp.vacaciones.infrastructure.persistence.SolicitudRepository;
import com.devcoreerp.backend_erp.vacaciones.infrastructure.persistence.VacacionRepository;
import com.devcoreerp.backend_erp.vacaciones.infrastructure.dtos.CreateSolicitudDTO;
import com.devcoreerp.backend_erp.vacaciones.infrastructure.dtos.ResponseSolicitudDTO;
import com.devcoreerp.backend_erp.vacaciones.application.mappers.*;
import com.devcoreerp.backend_erp.vacaciones.domain.Solicitud;
import com.devcoreerp.backend_erp.vacaciones.domain.Vacacion;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;
import java.time.temporal.ChronoUnit;
import java.time.LocalDate;
import java.time.ZoneId;

@Service
public class SolicitudService{

    private final SolicitudRepository solicitudRepository;
    private final VacacionRepository vacacionRepository;

    public SolicitudService(SolicitudRepository solicitudRepository, VacacionRepository vacacionRepository){
        this.solicitudRepository = solicitudRepository;
        this.vacacionRepository = vacacionRepository;
    }

    private long deltaDays(Date inicio, Date fin){
        ZoneId zone = ZoneId.systemDefault();
        LocalDate start = inicio.toInstant().atZone(zone).toLocalDate();
        LocalDate end   = fin.toInstant().atZone(zone).toLocalDate();
        return ChronoUnit.DAYS.between(start, end) + 1;
    }

    public ResponseSolicitudDTO aprobar(Long id, CreateSolicitudDTO dto){
        Solicitud solicitud = solicitudRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND, "Solicitud no encontrada"
        ));
        if (dto.estado() != "aprobado"){
            throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "Solicitud no aprobada" );
        }
        solicitud.setEstado(dto.estado());
        solicitud.setUpdatedAt(new Date());
        Vacacion vacacion = vacacionRepository.findById(dto.vacacionId())
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND, "Vacacion no encontrada"
        ));
        long days = deltaDays(dto.fechaInicio(), dto.fechaFin());
        if (days > vacacion.getDias()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sin dias suficientes");
        }
        vacacion.setDias(vacacion.getDias()-days);
        vacacionRepository.save(vacacion);
        Solicitud updated = solicitudRepository.save(solicitud);
        return SolicitudMapper.toDTO(updated);
    }

    public ResponseSolicitudDTO create(CreateSolicitudDTO dto){
        Vacacion vacacion = vacacionRepository.findById(dto.vacacionId())
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND, "Vacacion no encontrada"
        ));
        long days = deltaDays(dto.fechaInicio(), dto.fechaFin());
        if (days > vacacion.getDias()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sin dias suficientes");
        }
        Solicitud solicitud = SolicitudMapper.toEntity(dto,vacacion);
        Solicitud saved = solicitudRepository.save(solicitud);
        return SolicitudMapper.toDTO(saved);
    }

    public ResponseSolicitudDTO update(Long id, CreateSolicitudDTO dto){
        Solicitud solicitud = solicitudRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND, "Solicitud no encontrada"
        ));
        solicitud.setEstado(dto.estado());
        solicitud.setFechaInicio(dto.fechaInicio());
        solicitud.setFechaFin(dto.fechaFin());
        solicitud.setUpdatedAt(new Date());
        Solicitud updated = solicitudRepository.save(solicitud);
        return SolicitudMapper.toDTO(updated);
    }

    public ResponseSolicitudDTO delete(Long id){
        Solicitud solicitud = solicitudRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND, "Solicitud no encontrada"
        ));
        solicitudRepository.delete(solicitud);
        return SolicitudMapper.toDTO(solicitud);
    }

    @Transactional(readOnly = true)
    public ResponseSolicitudDTO findById(Long id) {
        Solicitud solicitud = solicitudRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND, "Solicitud no encontrada"
        ));
        return SolicitudMapper.toDTO(solicitud);
    }

    @Transactional(readOnly = true)
    public List<ResponseSolicitudDTO> findAll() {
        List<Solicitud> list = solicitudRepository.findAll();
        return list.stream().map(SolicitudMapper::toDTO).toList();
    }
}