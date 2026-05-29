package com.devcoreerp.backend_erp.auth.infrastructure.persistance;

import com.devcoreerp.backend_erp.auth.domain.Role;
import com.devcoreerp.backend_erp.auth.domain.RoleType;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    @EntityGraph(attributePaths = "permissions")
    Optional<Role> findByName(String name);

    @EntityGraph(attributePaths = "permissions")
    @Query("select r from Role r where r.id = :id")
    Optional<Role> findWithPermissionsById(@Param("id") Long id);

    @EntityGraph(attributePaths = "permissions")
    Set<Role> findByEstadoTrue();

    Set<Role> findByTipo(RoleType tipo);

    boolean existsByName(String name);

    Set<Role> findByTipoAndEstadoTrue(RoleType tipo);
}
