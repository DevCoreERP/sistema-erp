package com.devcoreerp.backend_erp.multitenancy;

public final class TenantContext {

    private static final ThreadLocal<TenantInfo> CURRENT_TENANT = new ThreadLocal<>();

    private TenantContext() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static void setCurrentTenant(String tenant) {
        CURRENT_TENANT.set(new TenantInfo(null, null, tenant));
    }

    public static void setCurrentTenant(Tenant tenant) {
        if (tenant == null) {
            clear();
            return;
        }
        CURRENT_TENANT.set(new TenantInfo(tenant.getId(), tenant.getSubdomain(), tenant.getSchemaName()));
    }

    public static String getCurrentTenant() {
        TenantInfo tenantInfo = CURRENT_TENANT.get();
        return tenantInfo == null ? null : tenantInfo.schemaName();
    }

    public static Long getCurrentTenantId() {
        TenantInfo tenantInfo = CURRENT_TENANT.get();
        return tenantInfo == null ? null : tenantInfo.tenantId();
    }

    public static String getCurrentTenantSubdomain() {
        TenantInfo tenantInfo = CURRENT_TENANT.get();
        return tenantInfo == null ? null : tenantInfo.subdomain();
    }

    public static void clear() {
        CURRENT_TENANT.remove();
    }

    private record TenantInfo(Long tenantId, String subdomain, String schemaName) {
    }
}
