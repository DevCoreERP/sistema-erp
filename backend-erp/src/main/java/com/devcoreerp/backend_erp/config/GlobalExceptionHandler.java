package com.devcoreerp.backend_erp.config;

import com.devcoreerp.backend_erp.multitenancy.exceptions.TenantException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatusException(
            ResponseStatusException ex) {

        Map<String, Object> response = new HashMap<>();

        response.put("status", ex.getStatusCode().value());
        response.put("error", ex.getReason());

        return ResponseEntity
                .status(ex.getStatusCode())
                .body(response);
    }

    @ExceptionHandler(TenantException.class)
    public ResponseEntity<Map<String, Object>> handleTenantException(TenantException ex) {
        Map<String, Object> response = new HashMap<>();

        response.put("status", ex.getStatus().value());
        response.put("error", ex.getMessage());

        return ResponseEntity
                .status(ex.getStatus())
                .body(response);
    }
}
