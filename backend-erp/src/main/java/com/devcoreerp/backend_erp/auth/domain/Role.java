package com.devcoreerp.backend_erp.auth.domain;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    BACK_OFFICE_ADMIN,
    SALES_MANAGER,
    END_USER;

    @Override
    public String getAuthority() {
        return "ROLE_" + name();
    }
}