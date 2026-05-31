package com.devcoreerp.backend_erp.multitenancy.dtos;

import com.devcoreerp.backend_erp.multitenancy.TenantStatus;
import java.time.LocalDateTime;

public record TenantResponseDTO(
        Long id,
        String name,
        String subdomain,
        String schemaName,
        TenantStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
