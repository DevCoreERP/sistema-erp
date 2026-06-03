package com.devcoreerp.backend_erp.pago.infrastructure.persistance;

import com.devcoreerp.backend_erp.pago.domain.TenantMetodoPago;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TenantMetodoPagoRepository extends JpaRepository<TenantMetodoPago, Long> {

    List<TenantMetodoPago> findByTenant_IdAndEstadoTrue(Long tenantId);

    Optional<TenantMetodoPago> findByIdAndTenant_Id(Long id, Long tenantId);

    boolean existsByTenant_IdAndEstadoTrue(Long tenantId);
}
