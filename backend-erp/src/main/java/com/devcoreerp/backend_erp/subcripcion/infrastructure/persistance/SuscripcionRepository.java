package com.devcoreerp.backend_erp.subcripcion.infrastructure.persistance;

import com.devcoreerp.backend_erp.subcripcion.domain.Suscripcion;
import com.devcoreerp.backend_erp.subcripcion.domain.SuscripcionEstado;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SuscripcionRepository extends JpaRepository<Suscripcion, Long> {

    @EntityGraph(attributePaths = {"plan", "plan.modulos", "plan.beneficios", "tenantMetodoPago"})
    Optional<Suscripcion> findFirstByTenant_IdAndEstadoAndFechaInicioLessThanEqualAndFechaFinGreaterThanEqualOrderByFechaFinDesc(
            Long tenantId,
            SuscripcionEstado estado,
            LocalDate fechaInicio,
            LocalDate fechaFin);

    @EntityGraph(attributePaths = {"plan", "tenantMetodoPago"})
    Optional<Suscripcion> findFirstByTenant_IdOrderByFechaCreacionDesc(Long tenantId);

    List<Suscripcion> findByTenant_IdAndEstadoIn(Long tenantId, Collection<SuscripcionEstado> estados);
}
