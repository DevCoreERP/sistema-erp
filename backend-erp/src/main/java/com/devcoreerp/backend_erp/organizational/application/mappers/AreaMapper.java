package com.devcoreerp.backend_erp.organizational.application.mappers;

import com.devcoreerp.backend_erp.organizational.infrastructure.dtos.CreateAreaDTO;
import com.devcoreerp.backend_erp.organizational.infrastructure.dtos.ResponseAreaDTO;
import com.devcoreerp.backend_erp.organizational.domain.Area;

public class AreaMapper {

    public static ResponseAreaDTO toDTO(Area area) {
        if (area == null) return null;

        return new ResponseAreaDTO(
                area.getId(),
                area.getNombre(),
                area.getActive(),
                area.getCreatedAt()
        );
    }

    public static Area toEntity(ResponseAreaDTO dto) {
        if (dto == null) return null;

        Area area = new Area(dto.nombre());
        area.setId(dto.id());
        area.setActive(dto.active());
        area.setCreatedAt(dto.createdAt());
        return area;
    }

    public static Area toEntity(CreateAreaDTO dto) {
        if (dto == null) return null;
        Area area = new Area(dto.nombre());
        return area;
    }
}