package com.devcoreerp.backend_erp.organizational.application.services;

import com.devcoreerp.backend_erp.organizational.infrastructure.persistence.DepartamentoRepository;
import com.devcoreerp.backend_erp.organizational.infrastructure.persistence.CargoRepository;
import com.devcoreerp.backend_erp.organizational.infrastructure.dtos.CreateCargoDTO;
import com.devcoreerp.backend_erp.organizational.infrastructure.dtos.ResponseCargoDTO;
import com.devcoreerp.backend_erp.organizational.application.mappers.CargoMapper;
import com.devcoreerp.backend_erp.organizational.domain.Cargo;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CargoService{

    private final CargoRepository cargoRepository;
    private final DepartamentoRepository departamentoRepository;

    public CargoService(CargoRepository cargoRepository, DepartamentoRepository departamentoRepository){
        this.cargoRepository = cargoRepository;
        this.departamentoRepository = departamentoRepository;
    }

    public ResponseCargoDTO create(CreateCargoDTO dto){
        Cargo cargo = CargoMapper.toEntity(dto,
            departamentoRepository.findById(dto.departamentoId())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Departamento no encontrado"
            ))
        );
        Cargo saved = cargoRepository.save(cargo);
        return CargoMapper.toDTO(saved);
    }

    public ResponseCargoDTO update(Long id, CreateCargoDTO dto){
        Cargo cargo = cargoRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND, "Cargo no encontrada"
        ));
        cargo.setNombre(dto.nombre());
        Cargo updated = cargoRepository.save(cargo);
        return CargoMapper.toDTO(updated);
    }

    public ResponseCargoDTO delete(Long id){
        Cargo cargo = cargoRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND, "Cargo no encontrada"
        ));
        cargoRepository.delete(cargo);
        return CargoMapper.toDTO(cargo);
    }

    @Transactional(readOnly = true)
    public ResponseCargoDTO findById(Long id) {
        Cargo cargo = cargoRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND, "Cargo no encontrada"
        ));
        return CargoMapper.toDTO(cargo);
    }

    @Transactional(readOnly = true)
    public List<ResponseCargoDTO> findAll() {
        List<Cargo> list = cargoRepository.findAll();
        return list.stream().map(CargoMapper::toDTO).toList();
    }
}