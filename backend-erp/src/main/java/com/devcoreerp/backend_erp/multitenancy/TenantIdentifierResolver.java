package com.devcoreerp.backend_erp.multitenancy;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.stereotype.Component;

@Component
public class TenantIdentifierResolver implements CurrentTenantIdentifierResolver<String> {

    @Override
    public String resolveCurrentTenantIdentifier() {
        String tenant = TenantContext.getCurrentTenant();
        return tenant == null || tenant.isBlank() ? TenantConstants.PUBLIC_SCHEMA : tenant;
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}
