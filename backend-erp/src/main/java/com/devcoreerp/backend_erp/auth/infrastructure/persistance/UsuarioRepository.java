package com.devcoreerp.backend_erp.auth.infrastructure.persistance;

import com.devcoreerp.backend_erp.auth.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

/**
 * UsuarioRepository: Acceso a datos para la entidad Usuario
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    /**
     * Busca un usuario por su nombre de usuario
     */
    Optional<Usuario> findByUsername(String username);
    
    /**
     * Busca un usuario por el email de su Persona
     */
    @Query("SELECT u FROM Usuario u WHERE u.persona.email = :email")
    Optional<Usuario> findByEmail(@Param("email") String email);
    
    /**
     * Busca todos los usuarios activos
     */
    Set<Usuario> findByEnabledTrue();
    
    /**
     * Busca usuarios por rol
     */
    Set<Usuario> findByRoleId(Long roleId);
    
    /**
     * Verifica si existe un usuario con el nombre de usuario dado
     */
    boolean existsByUsername(String username);
    
    /**
     * Verifica si existe un usuario con el email dado
     */
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM Usuario u WHERE u.persona.email = :email")
    boolean existsByEmail(@Param("email") String email);
}