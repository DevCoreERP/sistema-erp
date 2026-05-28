package com.devcoreerp.backend_erp.vacaciones.infrastructure.persistence;

import com.devcoreerp.backend_erp.vacaciones.domain.Saldo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface SaldoRepository extends JpaRepository<Saldo, Long> {
    /**
     * Busca una vacacion por usuarioId
     */
    Optional<Saldo> findByUsuario(Long usuarioId);

    /**
     * Busca una vacacion por numero de dias
     */
    List<Saldo> findByDias(Long dias);
}