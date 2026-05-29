package com.devcoreerp.backend_erp.auth.infrastructure.dtos;

import java.util.Set;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class UpdateRoleDTO {

    private String name;

    private String description;
    
    private Set<String> permissionCodes;
}
