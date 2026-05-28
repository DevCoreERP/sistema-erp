package com.devcoreerp.backend_erp.vacaciones.application.mappers;

import com.devcoreerp.backend_erp.vacaciones.infrastructure.dtos.CreateSaldoDTO;
import com.devcoreerp.backend_erp.vacaciones.infrastructure.dtos.ResponseSaldoDTO;

import com.devcoreerp.backend_erp.vacaciones.domain.Saldo;

public class SaldoMapper {

    public static ResponseSaldoDTO toDTO(Saldo saldo) {
        if (saldo == null) return null;

        return new ResponseSaldoDTO(
            saldo.getId(),
            saldo.getDias(),
            saldo.getCreatedAt(),
            saldo.getUpdatedAt(),
            saldo.getUsuario()
        );
    }

    public static Saldo toEntity(CreateSaldoDTO dto, Long usuario) {
        if (dto == null) return null;

        Saldo saldo = new Saldo(usuario, dto.dias());
        return saldo;
    }
}