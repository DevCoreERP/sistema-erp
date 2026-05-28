package com.devcoreerp.backend_erp.auth.infrastructure.persistance;

import com.devcoreerp.backend_erp.auth.domain.Permission;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    Optional<Permission> findByCode(String code);

    Set<Permission> findByEstadoTrue();

    boolean existsByCode(String code);
}
