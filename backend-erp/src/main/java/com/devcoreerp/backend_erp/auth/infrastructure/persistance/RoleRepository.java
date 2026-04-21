package com.devcoreerp.backend_erp.auth.infrastructure.persistance;

import com.devcoreerp.backend_erp.auth.domain.Role;
import com.devcoreerp.backend_erp.auth.domain.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

/**
 * RoleRepository: Acceso a datos para la entidad Role
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
    /**
     * Busca un rol por su nombre
     */
    Optional<Role> findByName(String name);
    
    /**
     * Busca todos los roles activos
     */
    Set<Role> findByActiveTrue();
    
    /**
     * Busca roles por tipo (SYSTEM o CUSTOM)
     */
    Set<Role> findByRoleType(RoleType roleType);
    
    /**
     * Verifica si existe un rol con el nombre dado
     */
    boolean existsByName(String name);
    
    /**
     * Busca roles activos de un tipo específico
     */
    Set<Role> findByRoleTypeAndActiveTrue(RoleType roleType);
}