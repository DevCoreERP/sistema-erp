package com.devcoreerp.backend_erp.multitenancy;

import com.devcoreerp.backend_erp.multitenancy.exceptions.InvalidTenantException;
import com.devcoreerp.backend_erp.multitenancy.exceptions.TenantInactiveException;
import com.devcoreerp.backend_erp.multitenancy.exceptions.TenantNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TenantSchemaResolver {

    private final TenantRepository tenantRepository;

    public TenantSchemaResolver(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    @Transactional(readOnly = true)
    public Tenant resolveActiveTenantBySubdomain(String subdomain) {
        String normalizedSubdomain = TenantValidator.normalizeSubdomain(subdomain);
        Tenant tenant = tenantRepository.findBySubdomain(normalizedSubdomain)
                .orElseThrow(() -> new TenantNotFoundException("Tenant no encontrado: " + normalizedSubdomain));
        return validateActiveTenant(tenant);
    }

    @Transactional(readOnly = true)
    public Tenant resolveActiveTenantById(Long tenantId) {
        if (tenantId == null) {
            throw new InvalidTenantException("tenantId requerido");
        }
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new TenantNotFoundException("Tenant no encontrado: " + tenantId));
        return validateActiveTenant(tenant);
    }

    @Transactional(readOnly = true)
    public Tenant resolveActiveTenantBySchemaName(String schemaName) {
        String validatedSchemaName = TenantValidator.validateSchemaName(schemaName);
        Tenant tenant = tenantRepository.findBySchemaName(validatedSchemaName)
                .orElseThrow(() -> new TenantNotFoundException("Tenant no encontrado para schema: " + validatedSchemaName));
        return validateActiveTenant(tenant);
    }

    private Tenant validateActiveTenant(Tenant tenant) {
        TenantValidator.validateSchemaName(tenant.getSchemaName());
        if (tenant.getStatus() != TenantStatus.ACTIVE) {
            throw new TenantInactiveException("Tenant no activo: " + tenant.getSubdomain());
        }
        return tenant;
    }
}
