package com.devcoreerp.backend_erp.auth.application.mapper;


import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.devcoreerp.backend_erp.auth.domain.Role;
import com.devcoreerp.backend_erp.auth.infrastructure.dtos.RoleResponseDTO;
import com.devcoreerp.backend_erp.auth.infrastructure.dtos.UpdateRoleDTO;

@Mapper(componentModel = "spring")
public interface RolMapper {

    RoleResponseDTO toResponseDTO(Role role);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "permissions", ignore = true)
    void updateFromDTO(UpdateRoleDTO dto, @MappingTarget Role role);

}
