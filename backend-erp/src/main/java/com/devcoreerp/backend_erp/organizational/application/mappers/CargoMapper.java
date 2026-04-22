package com.devcoreerp.backend_erp.organizational.application.mappers;

import com.devcoreerp.backend_erp.organizational.infrastructure.dtos.CreateCargoDTO;
import com.devcoreerp.backend_erp.organizational.infrastructure.dtos.ResponseCargoDTO;
import com.devcoreerp.backend_erp.organizational.domain.Cargo;
import com.devcoreerp.backend_erp.organizational.domain.Departamento;

public class CargoMapper {

    public static ResponseCargoDTO toDTO(Cargo cargo) {
        if (cargo == null) return null;

        return new ResponseCargoDTO(
            cargo.getId(),
            cargo.getNombre(),
            cargo.getActive(),
            cargo.getCreatedAt(),
            cargo.getDepartamento()
        );
    }

    public static Cargo toEntity(ResponseCargoDTO dto) {
        if (dto == null) return null;

        Cargo cargo = new Cargo(dto.nombre(), dto.departamento());
        cargo.setId(dto.id());
        cargo.setActive(dto.active());
        cargo.setCreatedAt(dto.createdAt());
        cargo.setDepartamento(dto.departamento());
        return cargo;
    }

    public static Cargo toEntity(CreateCargoDTO dto, Departamento departamento) {
        if (dto == null) return null;
        Cargo cargo = new Cargo(dto.nombre(), departamento);
        return cargo;
    }
}