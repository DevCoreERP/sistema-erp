package com.devcoreerp.backend_erp.control_asistencia.application.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.devcoreerp.backend_erp.control_asistencia.domain.Turno;
import com.devcoreerp.backend_erp.control_asistencia.infrastructure.dto.TurnoRequestDTO;
import com.devcoreerp.backend_erp.control_asistencia.infrastructure.dto.TurnoResponseDTO;

@Mapper(componentModel = "spring")
public interface TurnoMapper {
    TurnoResponseDTO toResponseDTO(Turno turno);

    Turno toEntity(TurnoRequestDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "asignacionesTurno", ignore = true)
    void updateFromDTO(TurnoRequestDTO dto, @MappingTarget Turno turno);
    // AsignacionTurnoResponseDTO toAsignacionResponseDTO(AsignacionTurno
    // asignacion);
}