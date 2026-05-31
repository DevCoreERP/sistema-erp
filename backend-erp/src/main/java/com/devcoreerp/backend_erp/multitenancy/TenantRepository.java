package com.devcoreerp.backend_erp.multitenancy;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {

    Optional<Tenant> findBySubdomain(String subdomain);

    Optional<Tenant> findBySubdomainAndStatus(String subdomain, TenantStatus status);

    Optional<Tenant> findBySchemaName(String schemaName);

    Optional<Tenant> findBySchemaNameAndStatus(String schemaName, TenantStatus status);

    Optional<Tenant> findByIdAndStatus(Long id, TenantStatus status);

    boolean existsBySubdomain(String subdomain);

    boolean existsBySchemaName(String schemaName);
}
