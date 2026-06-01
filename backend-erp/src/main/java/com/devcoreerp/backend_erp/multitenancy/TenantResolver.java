package com.devcoreerp.backend_erp.multitenancy;

import com.devcoreerp.backend_erp.multitenancy.exceptions.InvalidTenantException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class TenantResolver {

    private final Environment environment;
    private final String tenantHeaderName;
    private final boolean allowHeaderTenant;

    public TenantResolver(
            Environment environment,
            @Value("${application.multitenancy.tenant-header:" + TenantConstants.DEFAULT_TENANT_HEADER + "}") String tenantHeaderName,
            @Value("${application.multitenancy.allow-header-tenant:false}") boolean allowHeaderTenant) {
        this.environment = environment;
        this.tenantHeaderName = tenantHeaderName;
        this.allowHeaderTenant = allowHeaderTenant;
    }

    public String resolveSubdomain(HttpServletRequest request) {
        // Este es el caso de Produccion
        Optional<String> hostSubdomain = resolveFromHost(request);
        if (hostSubdomain.isPresent()) {
            return TenantValidator.normalizeSubdomain(hostSubdomain.get());
        }

        // Cuando es en desarollo en localhost
        if (isHeaderTenantAllowed(request)) {
            String headerValue = request.getHeader(tenantHeaderName);
            if (headerValue != null && !headerValue.isBlank()) {
                return TenantValidator.normalizeSubdomain(headerValue);
            }
        }

        throw new InvalidTenantException("No se pudo resolver el tenant de la request");
    }

    private Optional<String> resolveFromHost(HttpServletRequest request) {
        String host = request.getHeader("Host");
        if (host == null || host.isBlank()) {
            host = request.getServerName();
        }
        if (host == null || host.isBlank()) {
            return Optional.empty();
        }

        String normalizedHost = stripPort(host).toLowerCase(Locale.ROOT);
        if (normalizedHost.endsWith(".localhost")) {
            return Optional.of(firstLabel(normalizedHost));
        }
        if (isLocalHost(normalizedHost) || !normalizedHost.contains(".")) {
            return Optional.empty();
        }
        return Optional.of(firstLabel(normalizedHost));
    }

    private boolean isHeaderTenantAllowed(HttpServletRequest request) {
        if (!allowHeaderTenant) {
            return false;
        }
        String host = request.getHeader("Host");
        if (host == null || host.isBlank()) {
            host = request.getServerName();
        }
        String normalizedHost = host == null ? "" : stripPort(host).toLowerCase(Locale.ROOT);
        return isLocalHost(normalizedHost) || isDevProfileActive();
    }

    private boolean isDevProfileActive() {
        return Arrays.stream(environment.getActiveProfiles())
                .anyMatch(profile -> profile.equalsIgnoreCase("dev") || profile.equalsIgnoreCase("local"));
    }

    private boolean isLocalHost(String host) {
        return "localhost".equals(host)
                || "127.0.0.1".equals(host)
                || "0.0.0.0".equals(host)
                || "::1".equals(host);
    }

    private String stripPort(String host) {
        int portSeparatorIndex = host.indexOf(':');
        return portSeparatorIndex >= 0 ? host.substring(0, portSeparatorIndex) : host;
    }

    private String firstLabel(String host) {
        int dotIndex = host.indexOf('.');
        return dotIndex >= 0 ? host.substring(0, dotIndex) : host;
    }
}
