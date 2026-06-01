package com.devcoreerp.backend_erp.multitenancy.exceptions;

import org.springframework.http.HttpStatus;

public class TenantNotFoundException extends TenantException {

    public TenantNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
