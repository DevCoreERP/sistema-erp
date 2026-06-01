package com.devcoreerp.backend_erp.pago.application.services;

import com.devcoreerp.backend_erp.multitenancy.TenantContext;
import com.devcoreerp.backend_erp.pago.domain.Pago;
import com.devcoreerp.backend_erp.pago.infrastructure.dtos.PagoResponseDTO;
import com.devcoreerp.backend_erp.pago.infrastructure.persistance.PagoRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class PagoService {

    private final PagoRepository pagoRepository;

    public PagoService(PagoRepository pagoRepository) {
        this.pagoRepository = pagoRepository;
    }

    public List<PagoResponseDTO> listarHistorialTenantActual() {
        Long tenantId = TenantContext.getCurrentTenantId();
        if (tenantId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Tenant no resuelto");
        }

        return pagoRepository.findByTenant_IdOrderByFechaPagoDesc(tenantId)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    private PagoResponseDTO mapToDTO(Pago pago) {
        return new PagoResponseDTO(
                pago.getId(),
                pago.getSuscripcion().getId(),
                pago.getTenantMetodoPago().getId(),
                pago.getTenantMetodoPago().getMetodoPago().getNombre(),
                pago.getEstado(),
                pago.getMontoPagadoUsd(),
                pago.getFechaPago(),
                pago.getCodigoPago(),
                pago.getObservacion(),
                pago.getFechaCreacion());
    }
}
