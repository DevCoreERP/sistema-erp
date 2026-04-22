package com.devcoreerp.backend_erp.organizational.application.services;

import com.devcoreerp.backend_erp.organizational.infrastructure.persistence.AreaRepository;
import com.devcoreerp.backend_erp.organizational.infrastructure.persistence.DepartamentoRepository;
import com.devcoreerp.backend_erp.organizational.infrastructure.dtos.CreateDepartamentoDTO;
import com.devcoreerp.backend_erp.organizational.infrastructure.dtos.ResponseDepartamentoDTO;
import com.devcoreerp.backend_erp.organizational.application.mappers.DepartamentoMapper;
import com.devcoreerp.backend_erp.organizational.domain.Departamento;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class DepartamentoService{

    private final DepartamentoRepository departamentoRepository;
    private final AreaRepository areaRepository;

    public DepartamentoService(DepartamentoRepository departamentoRepository, AreaRepository areaRepository){
        this.departamentoRepository = departamentoRepository;
        this.areaRepository = areaRepository;
    }

    public ResponseDepartamentoDTO create(CreateDepartamentoDTO dto){
        Departamento departamento = DepartamentoMapper.toEntity(dto,
            areaRepository.findById(dto.areaId())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Area no encontrada"
            ))
        );
        Departamento saved = departamentoRepository.save(departamento);
        return DepartamentoMapper.toDTO(saved);
    }

    public ResponseDepartamentoDTO update(Long id, CreateDepartamentoDTO dto){
        Departamento departamento = departamentoRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND, "Departamento no encontrada"
        ));
        departamento.setNombre(dto.nombre());
        Departamento updated = departamentoRepository.save(departamento);
        return DepartamentoMapper.toDTO(updated);
    }

    public ResponseDepartamentoDTO delete(Long id){
        Departamento departamento = departamentoRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND, "Departamento no encontrada"
        ));
        departamentoRepository.delete(departamento);
        return DepartamentoMapper.toDTO(departamento);
    }

    @Transactional(readOnly = true)
    public ResponseDepartamentoDTO findById(Long id) {
        Departamento departamento = departamentoRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND, "Departamento no encontrada"
        ));
        return DepartamentoMapper.toDTO(departamento);
    }

    @Transactional(readOnly = true)
    public List<ResponseDepartamentoDTO> findAll() {
        List<Departamento> list = departamentoRepository.findAll();
        return list.stream().map(DepartamentoMapper::toDTO).toList();
    }
}