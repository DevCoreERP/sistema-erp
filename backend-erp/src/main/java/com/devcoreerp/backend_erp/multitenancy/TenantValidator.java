package com.devcoreerp.backend_erp.multitenancy;

import com.devcoreerp.backend_erp.multitenancy.exceptions.InvalidSchemaException;
import com.devcoreerp.backend_erp.multitenancy.exceptions.InvalidTenantException;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

public final class TenantValidator {

    private static final Pattern SUBDOMAIN_PATTERN =
            Pattern.compile("^[a-z0-9](?:[a-z0-9-]{0,78}[a-z0-9])?$");
    private static final Pattern SCHEMA_PATTERN =
            Pattern.compile("^(public|tenant_[a-z0-9_]{1,113})$");
    private static final Set<String> RESERVED_SUBDOMAINS = Set.of("www", "api", "admin", "public");

    private TenantValidator() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static String normalizeSubdomain(String subdomain) {
        if (subdomain == null) {
            throw new InvalidTenantException("Subdominio requerido");
        }
        String normalized = subdomain.trim().toLowerCase(Locale.ROOT);
        if (!isValidSubdomain(normalized)) {
            throw new InvalidTenantException("Subdominio invalido: " + subdomain);
        }
        return normalized;
    }

    public static boolean isValidSubdomain(String subdomain) {
        return subdomain != null
                && SUBDOMAIN_PATTERN.matcher(subdomain).matches()
                && !RESERVED_SUBDOMAINS.contains(subdomain);
    }

    public static String schemaNameForSubdomain(String subdomain) {
        String normalizedSubdomain = normalizeSubdomain(subdomain);
        return TenantConstants.TENANT_SCHEMA_PREFIX + normalizedSubdomain.replace('-', '_');
    }

    public static String validateSchemaName(String schemaName) {
        if (schemaName == null || !SCHEMA_PATTERN.matcher(schemaName).matches()) {
            throw new InvalidSchemaException("Schema invalido: " + schemaName);
        }
        return schemaName;
    }

    public static String quotedSchemaName(String schemaName) {
        String validated = validateSchemaName(schemaName);
        if (TenantConstants.PUBLIC_SCHEMA.equals(validated)) {
            return TenantConstants.PUBLIC_SCHEMA;
        }
        return "\"" + validated + "\"";
    }
}
