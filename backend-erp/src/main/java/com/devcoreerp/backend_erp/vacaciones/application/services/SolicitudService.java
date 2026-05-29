package com.devcoreerp.backend_erp.vacaciones.application.services;

import com.devcoreerp.backend_erp.vacaciones.infrastructure.persistence.SolicitudRepository;
import com.devcoreerp.backend_erp.vacaciones.infrastructure.persistence.SaldoRepository;
import com.devcoreerp.backend_erp.vacaciones.infrastructure.dtos.CreateSolicitudDTO;
import com.devcoreerp.backend_erp.vacaciones.infrastructure.dtos.ResponseSolicitudDTO;
import com.devcoreerp.backend_erp.vacaciones.application.mappers.*;
import com.devcoreerp.backend_erp.vacaciones.domain.Solicitud;
import com.devcoreerp.backend_erp.vacaciones.domain.Saldo;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;
import java.time.temporal.ChronoUnit;
import java.time.LocalDate;

@Service
public class SolicitudService{

    private final SolicitudRepository solicitudRepository;
    private final SaldoRepository saldoRepository;

    public SolicitudService(SolicitudRepository solicitudRepository, SaldoRepository saldoRepository){
        this.solicitudRepository = solicitudRepository;
        this.saldoRepository = saldoRepository;
    }

    private long deltaDays(LocalDate inicio, LocalDate fin){
        return ChronoUnit.DAYS.between(inicio, fin) + 1;
    }

    public ResponseSolicitudDTO aprobar(Long id){
        Solicitud solicitud = solicitudRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND, "Solicitud no encontrada"
        ));
        solicitud.setEstado("aprobado");
        System.out.println("HELLO: APROBADO");
        // solicitud.setUpdatedAt(new Date());
        Saldo saldo = saldoRepository.findById(solicitud.getSaldo())
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND, "Vacacion no encontrada"
        ));
        System.out.println("HELLO: GET SALDO");
        System.out.println("HELLO: " + solicitud.getFechaInicio().getClass());
        long days = deltaDays(solicitud.getFechaInicio(), solicitud.getFechaFin());
        System.out.println("HELLO: GET DAYS");
        if (days > saldo.getDias()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sin dias suficientes");
        }
        saldo.setDias(saldo.getDias()-days);
        System.out.println("HELLO: SET SALDO");
        saldoRepository.save(saldo);
        Solicitud updated = solicitudRepository.save(solicitud);
        System.out.println("HELLO: SAVED");
        return SolicitudMapper.toDTO(updated);
    }

    public ResponseSolicitudDTO create(CreateSolicitudDTO dto){
        Saldo saldo = saldoRepository.findById(dto.saldoId())
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND, "Vacacion no encontrada"
        ));
        long days = deltaDays(dto.fechaInicio(), dto.fechaFin());
        if (days > saldo.getDias()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sin dias suficientes");
        }
        Solicitud solicitud = SolicitudMapper.toEntity(dto,dto.saldoId());
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