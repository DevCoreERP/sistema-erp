package com.devcoreerp.backend_erp.auth.infrastructure.persistance;

import com.devcoreerp.backend_erp.auth.domain.Permission;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    Optional<Permission> findByCode(String code);

    Set<Permission> findByEstadoTrue();

    boolean existsByCode(String code);

    @Query(value = """
            SELECT DISTINCT permissions.code
            FROM public.permissions permissions
            JOIN public.modulo modulo ON modulo.id = permissions.modulo_id
            JOIN public.plan_modulo plan_modulo ON plan_modulo.modulo_id = modulo.id
            WHERE plan_modulo.plan_id = :planId
              AND permissions.estado = TRUE
              AND modulo.estado = TRUE
            """, nativeQuery = true)
    Set<String> findActiveCodesAllowedByPlanId(@Param("planId") Long planId);
}
