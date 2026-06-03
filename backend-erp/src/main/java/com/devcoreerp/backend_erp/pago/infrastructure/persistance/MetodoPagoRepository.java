package com.devcoreerp.backend_erp.pago.infrastructure.persistance;

import com.devcoreerp.backend_erp.pago.domain.MetodoPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MetodoPagoRepository extends JpaRepository<MetodoPago, Long> {
}
