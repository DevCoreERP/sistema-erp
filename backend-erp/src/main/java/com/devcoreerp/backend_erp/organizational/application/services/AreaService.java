package com.devcoreerp.backend_erp.organizational.application.services;

import com.devcoreerp.backend_erp.organizational.infrastructure.persistence.AreaRepository;
import com.devcoreerp.backend_erp.organizational.infrastructure.dtos.CreateAreaDTO;
import com.devcoreerp.backend_erp.organizational.infrastructure.dtos.ResponseAreaDTO;
import com.devcoreerp.backend_erp.organizational.application.mappers.AreaMapper;
import com.devcoreerp.backend_erp.organizational.domain.Area;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class AreaService{

    private final AreaRepository areaRepository;

    public AreaService(AreaRepository areaRepository){
        this.areaRepository = areaRepository;
    }

    public ResponseAreaDTO create(CreateAreaDTO dto){
        Area area = AreaMapper.toEntity(dto);
        Area saved = areaRepository.save(area);
        return AreaMapper.toDTO(saved);
    }

    public ResponseAreaDTO update(Long id, CreateAreaDTO dto){
        Area area = areaRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND, "Area no encontrada"
        ));
        area.setNombre(dto.nombre());
        Area updated = areaRepository.save(area);
        return AreaMapper.toDTO(updated);
    }

    public ResponseAreaDTO delete(Long id){
        Area area = areaRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND, "Area no encontrada"
        ));
        areaRepository.delete(area);
        return AreaMapper.toDTO(area);
    }

    @Transactional(readOnly = true)
    public ResponseAreaDTO findById(Long id) {
        Area area = areaRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND, "Area no encontrada"
        ));
        return AreaMapper.toDTO(area);
    }

    @Transactional(readOnly = true)
    public List<ResponseAreaDTO> findAll() {
        List<Area> list = areaRepository.findAll();
        return list.stream().map(AreaMapper::toDTO).toList();
    }
}