package com.devcoreerp.backend_erp.plan.infrastructure.persistance;

import com.devcoreerp.backend_erp.plan.domain.Modulo;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModuloRepository extends JpaRepository<Modulo, Long> {

    Optional<Modulo> findByNombre(String nombre);
}
