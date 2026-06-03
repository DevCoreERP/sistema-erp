package com.devcoreerp.backend_erp.multitenancy.exceptions;

import org.springframework.http.HttpStatus;

public class TenantProvisioningException extends TenantException {

    public TenantProvisioningException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    public TenantProvisioningException(String message, Throwable cause) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message, cause);
    }
}
