package com.devcoreerp.backend_erp.multitenancy;

import java.util.Map;
import org.hibernate.cfg.MultiTenancySettings;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HibernateMultiTenantConfig {

    @Bean
    HibernatePropertiesCustomizer hibernatePropertiesCustomizer(
            MultiTenantConnectionProviderImpl connectionProvider,
            TenantIdentifierResolver tenantIdentifierResolver) {
        return (Map<String, Object> hibernateProperties) -> {
            hibernateProperties.put(MultiTenancySettings.MULTI_TENANT_CONNECTION_PROVIDER, connectionProvider);
            hibernateProperties.put(MultiTenancySettings.MULTI_TENANT_IDENTIFIER_RESOLVER, tenantIdentifierResolver);
            hibernateProperties.put(MultiTenancySettings.TENANT_IDENTIFIER_TO_USE_FOR_ANY_KEY, TenantConstants.PUBLIC_SCHEMA);
        };
    }
}
