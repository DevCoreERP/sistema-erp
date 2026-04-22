package com.devcoreerp.backend_erp.organizational.application.mappers;

import com.devcoreerp.backend_erp.organizational.infrastructure.dtos.CreateDepartamentoDTO;
import com.devcoreerp.backend_erp.organizational.infrastructure.dtos.ResponseDepartamentoDTO;
import com.devcoreerp.backend_erp.organizational.domain.Departamento;
import com.devcoreerp.backend_erp.organizational.domain.Area;

public class DepartamentoMapper {

    public static ResponseDepartamentoDTO toDTO(Departamento departamento) {
        if (departamento == null) return null;

        return new ResponseDepartamentoDTO(
            departamento.getId(),
            departamento.getNombre(),
            departamento.getActive(),
            departamento.getCreatedAt(),
            departamento.getArea()
        );
    }

    public static Departamento toEntity(ResponseDepartamentoDTO dto) {
        if (dto == null) return null;

        Departamento departamento = new Departamento(dto.nombre(), dto.area());
        departamento.setId(dto.id());
        departamento.setActive(dto.active());
        departamento.setCreatedAt(dto.createdAt());
        departamento.setArea(dto.area());
        return departamento;
    }

    public static Departamento toEntity(CreateDepartamentoDTO dto, Area area) {
        if (dto == null) return null;
        Departamento departamento = new Departamento(dto.nombre(), area);
        return departamento;
    }
}