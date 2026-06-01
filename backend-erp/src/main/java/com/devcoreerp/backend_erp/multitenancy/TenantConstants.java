package com.devcoreerp.backend_erp.multitenancy;

public final class TenantConstants {

    public static final String PUBLIC_SCHEMA = "public";
    public static final String TENANT_SCHEMA_PREFIX = "tenant_";
    public static final String DEFAULT_TENANT_HEADER = "X-Tenant-Subdomain";
    public static final String PROVISIONING_HEADER = "X-Provisioning-Key";
    public static final String JWT_TENANT_ID_CLAIM = "tenantId";
    public static final String JWT_TENANT_SUBDOMAIN_CLAIM = "tenantSubdomain";

    private TenantConstants() {
        throw new UnsupportedOperationException("Utility class");
    }
}
