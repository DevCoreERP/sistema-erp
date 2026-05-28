package com.devcoreerp.backend_erp.organizational.application.mappers;

import com.devcoreerp.backend_erp.organizational.infrastructure.dtos.CreateDepartamentoDTO;
import com.devcoreerp.backend_erp.organizational.infrastructure.dtos.ResponseDepartamentoDTO;
import com.devcoreerp.backend_erp.organizational.domain.Departamento;

public class DepartamentoMapper {

    public static ResponseDepartamentoDTO toDTO(Departamento departamento) {
        if (departamento == null) return null;

        return new ResponseDepartamentoDTO(
            departamento.getId(),
            departamento.getNombre(),
            departamento.getActive(),
            departamento.getCreatedAt(),
            departamento.getPadre()
        );
    }

    public static Departamento toEntity(ResponseDepartamentoDTO dto) {
        if (dto == null) return null;

        Departamento departamento = new Departamento(dto.nombre(), dto.padre());
        departamento.setId(dto.id());
        departamento.setActive(dto.active());
        departamento.setCreatedAt(dto.createdAt());
        departamento.setPadre(dto.padre());
        return departamento;
    }

    public static Departamento toEntity(CreateDepartamentoDTO dto) {
        if (dto == null) return null;
        Departamento departamento = new Departamento(dto.nombre(), dto.padreId());
        return departamento;
    }
}