package com.devcoreerp.backend_erp.auth.application.mapper;

import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.devcoreerp.backend_erp.auth.domain.Role;
import com.devcoreerp.backend_erp.auth.domain.Usuario;
import com.devcoreerp.backend_erp.auth.infrastructure.dtos.UsuarioResponseDTO;
import com.devcoreerp.backend_erp.auth.infrastructure.dtos.UsuarioUpdateDTO;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    //target=origen dto source=referncia entidad
    @Mapping(target = "roleNames", source = "roles")
    UsuarioResponseDTO toResponseDTO(Usuario usuario);

    // Helper para convertir el objeto Role a String
    default Set<String> mapRolesToStrings(Set<Role> roles) {
        if (roles == null)
            return null;
        return roles.stream().map(Role::getName).collect(Collectors.toSet());
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "roles", ignore = true) // roles se manejan en el service
    void updateFromDTO(UsuarioUpdateDTO dto, @MappingTarget Usuario usuario);

}
