package com.devcoreerp.backend_erp.multitenancy.exceptions;

import org.springframework.http.HttpStatus;

public class TenantInactiveException extends TenantException {

    public TenantInactiveException(String message) {
        super(HttpStatus.FORBIDDEN, message);
    }
}
