package com.devcoreerp.backend_erp.auth.infrastructure.persistance;

import com.devcoreerp.backend_erp.auth.domain.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

/**
 * PermissionRepository: Acceso a datos para la entidad Permission
 */
@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    
    /**
     * Busca un permiso por su código (ej: PRODUCT_CREATE)
     */
    Optional<Permission> findByCode(String code);
    
    /**
     * Busca todos los permisos de una categoría
     */
    Set<Permission> findByCategory(String category);
    
    /**
     * Verifica si existe un permiso con el código dado
     */
    boolean existsByCode(String code);
}