package com.devcoreerp.backend_erp.auth.infrastructure.persistance;

import com.devcoreerp.backend_erp.auth.domain.Usuario;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    @EntityGraph(attributePaths = {"roles", "roles.permissions"})
    Optional<Usuario> findByUsername(String username);

    @EntityGraph(attributePaths = {"roles", "roles.permissions"})
    Optional<Usuario> findByEmail(String email);

    @EntityGraph(attributePaths = {"roles", "roles.permissions"})
    @Query("select u from Usuario u where u.id = :id")
    Optional<Usuario> findWithRolesById(@Param("id") Long id);

    Set<Usuario> findByEnabledTrue();

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
