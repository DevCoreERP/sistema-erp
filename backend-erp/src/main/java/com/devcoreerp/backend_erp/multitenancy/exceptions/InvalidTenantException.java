package com.devcoreerp.backend_erp.multitenancy.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidTenantException extends TenantException {

    public InvalidTenantException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
