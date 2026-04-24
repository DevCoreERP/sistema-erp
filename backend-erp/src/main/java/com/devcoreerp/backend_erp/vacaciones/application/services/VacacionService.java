package com.devcoreerp.backend_erp.vacaciones.application.services;

import com.devcoreerp.backend_erp.vacaciones.infrastructure.persistence.VacacionRepository;
import com.devcoreerp.backend_erp.vacaciones.infrastructure.dtos.CreateVacacionDTO;
import com.devcoreerp.backend_erp.vacaciones.infrastructure.dtos.ResponseVacacionDTO;
import com.devcoreerp.backend_erp.empleados.infrastructure.persistance.EmpleadoRepository;
import com.devcoreerp.backend_erp.vacaciones.application.mappers.VacacionMapper;
import com.devcoreerp.backend_erp.vacaciones.domain.Vacacion;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;

@Service
public class VacacionService{

    private final VacacionRepository vacacionRepository;
    private final EmpleadoRepository empleadoRepository;

    public VacacionService(VacacionRepository vacacionRepository, EmpleadoRepository empleadoRepository){
        this.vacacionRepository = vacacionRepository;
        this.empleadoRepository = empleadoRepository;
    }

    public ResponseVacacionDTO create(CreateVacacionDTO dto){
        Vacacion vacacion = VacacionMapper.toEntity(dto,
            empleadoRepository.findById(dto.empleadoId())
            .orElseThrow( () -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Empleado no encontrado"
            ))
        );
        Vacacion saved = vacacionRepository.save(vacacion);
        return VacacionMapper.toDTO(saved);
    }

    public ResponseVacacionDTO update(Long id, CreateVacacionDTO dto){
        Vacacion vacacion = vacacionRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND, "Vacacion no encontrada"
        ));
        vacacion.setDias(dto.dias());
        vacacion.setUpdatedAt(new Date());
        Vacacion updated = vacacionRepository.save(vacacion);
        return VacacionMapper.toDTO(updated);
    }

    public ResponseVacacionDTO delete(Long id){
        Vacacion vacacion = vacacionRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND, "Vacacion no encontrada"
        ));
        vacacionRepository.delete(vacacion);
        return VacacionMapper.toDTO(vacacion);
    }

    @Transactional(readOnly = true)
    public ResponseVacacionDTO findById(Long id) {
        Vacacion vacacion = vacacionRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND, "Vacacion no encontrada"
        ));
        return VacacionMapper.toDTO(vacacion);
    }

    @Transactional(readOnly = true)
    public List<ResponseVacacionDTO> findAll() {
        List<Vacacion> list = vacacionRepository.findAll();
        return list.stream().map(VacacionMapper::toDTO).toList();
    }
}