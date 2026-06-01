package com.devcoreerp.backend_erp.pago.infrastructure.persistance;

import com.devcoreerp.backend_erp.pago.domain.Pago;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {

    List<Pago> findByTenant_IdOrderByFechaPagoDesc(Long tenantId);
}
