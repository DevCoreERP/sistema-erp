package com.devcoreerp.backend_erp.plan.infrastructure.persistance;

import com.devcoreerp.backend_erp.plan.domain.Plan;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {

    @EntityGraph(attributePaths = {"modulos", "beneficios"})
    @Query("select distinct plan from Plan plan where plan.estado = true order by plan.precioUsd asc")
    List<Plan> findByEstadoTrueOrderByPrecioUsdAsc();

    @EntityGraph(attributePaths = {"modulos", "beneficios"})
    Optional<Plan> findByIdAndEstadoTrue(Long id);

    @EntityGraph(attributePaths = {"modulos", "beneficios"})
    Optional<Plan> findByNombreAndEstadoTrue(String nombre);
}
