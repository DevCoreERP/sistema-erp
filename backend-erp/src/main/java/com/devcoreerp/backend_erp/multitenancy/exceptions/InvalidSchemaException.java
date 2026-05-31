package com.devcoreerp.backend_erp.multitenancy.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidSchemaException extends TenantException {

    public InvalidSchemaException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
