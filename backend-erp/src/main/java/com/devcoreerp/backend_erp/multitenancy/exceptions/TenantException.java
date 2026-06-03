package com.devcoreerp.backend_erp.multitenancy.exceptions;

import org.springframework.http.HttpStatus;

public abstract class TenantException extends RuntimeException {

    private final HttpStatus status;

    protected TenantException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    protected TenantException(HttpStatus status, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
