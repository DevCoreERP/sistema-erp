package com.devcoreerp.backend_erp.pago.application.services;

import com.devcoreerp.backend_erp.multitenancy.Tenant;
import com.devcoreerp.backend_erp.multitenancy.TenantContext;
import com.devcoreerp.backend_erp.multitenancy.TenantRepository;
import com.devcoreerp.backend_erp.pago.domain.MetodoPago;
import com.devcoreerp.backend_erp.pago.domain.TenantMetodoPago;
import com.devcoreerp.backend_erp.pago.infrastructure.dtos.TenantMetodoPagoRequestDTO;
import com.devcoreerp.backend_erp.pago.infrastructure.dtos.TenantMetodoPagoResponseDTO;
import com.devcoreerp.backend_erp.pago.infrastructure.persistance.MetodoPagoRepository;
import com.devcoreerp.backend_erp.pago.infrastructure.persistance.TenantMetodoPagoRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class TenantMetodoPagoService {

    private final TenantRepository tenantRepository;
    private final MetodoPagoRepository metodoPagoRepository;
    private final TenantMetodoPagoRepository tenantMetodoPagoRepository;

    public TenantMetodoPagoService(
            TenantRepository tenantRepository,
            MetodoPagoRepository metodoPagoRepository,
            TenantMetodoPagoRepository tenantMetodoPagoRepository) {
        this.tenantRepository = tenantRepository;
        this.metodoPagoRepository = metodoPagoRepository;
        this.tenantMetodoPagoRepository = tenantMetodoPagoRepository;
    }

    public TenantMetodoPagoResponseDTO registrar(TenantMetodoPagoRequestDTO dto) {
        Long tenantId = requireTenantId();
        Tenant tenant = getTenant(tenantId);
        MetodoPago metodoPago = getMetodoPagoActivo(dto.metodoPagoId());
        boolean esPrincipal = Boolean.TRUE.equals(dto.esPrincipal())
                || !tenantMetodoPagoRepository.existsByTenant_IdAndEstadoTrue(tenantId);

        if (esPrincipal) {
            desmarcarPrincipales(tenantId);
        }

        TenantMetodoPago tenantMetodoPago = TenantMetodoPago.builder()
                .tenant(tenant)
                .metodoPago(metodoPago)
                .titular(dto.titular().trim())
                .ultimosDigitos(extractLastDigits(dto.ultimosDigitos()))
                .marca(trimToNull(dto.marca()))
                .referencia(trimToNull(dto.referencia()))
                .esPrincipal(esPrincipal)
                .estado(dto.estado() == null || Boolean.TRUE.equals(dto.estado()))
                .build();

        return mapToDTO(tenantMetodoPagoRepository.save(tenantMetodoPago));
    }

    public TenantMetodoPagoResponseDTO actualizar(Long id, TenantMetodoPagoRequestDTO dto) {
        Long tenantId = requireTenantId();
        TenantMetodoPago tenantMetodoPago = tenantMetodoPagoRepository.findByIdAndTenant_Id(id, tenantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Metodo de pago del tenant no encontrado"));

        MetodoPago metodoPago = getMetodoPagoActivo(dto.metodoPagoId());
        if (Boolean.TRUE.equals(dto.esPrincipal())) {
            desmarcarPrincipales(tenantId);
            tenantMetodoPago.setEsPrincipal(true);
        } else if (dto.esPrincipal() != null) {
            tenantMetodoPago.setEsPrincipal(false);
        }

        tenantMetodoPago.setMetodoPago(metodoPago);
        tenantMetodoPago.setTitular(dto.titular().trim());
        tenantMetodoPago.setUltimosDigitos(extractLastDigits(dto.ultimosDigitos()));
        tenantMetodoPago.setMarca(trimToNull(dto.marca()));
        tenantMetodoPago.setReferencia(trimToNull(dto.referencia()));
        if (dto.estado() != null) {
            tenantMetodoPago.setEstado(dto.estado());
        }

        return mapToDTO(tenantMetodoPagoRepository.save(tenantMetodoPago));
    }

    @Transactional(readOnly = true)
    public List<TenantMetodoPagoResponseDTO> listarActivos() {
        return tenantMetodoPagoRepository.findByTenant_IdAndEstadoTrue(requireTenantId())
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    private void desmarcarPrincipales(Long tenantId) {
        tenantMetodoPagoRepository.findByTenant_IdAndEstadoTrue(tenantId)
                .forEach(existing -> {
                    existing.setEsPrincipal(false);
                    tenantMetodoPagoRepository.save(existing);
                });
    }

    private Tenant getTenant(Long tenantId) {
        return tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tenant no encontrado"));
    }

    private MetodoPago getMetodoPagoActivo(Long metodoPagoId) {
        MetodoPago metodoPago = metodoPagoRepository.findById(metodoPagoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Metodo de pago no encontrado"));

        if (!Boolean.TRUE.equals(metodoPago.getEstado())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Metodo de pago inactivo");
        }
        return metodoPago;
    }

    private Long requireTenantId() {
        Long tenantId = TenantContext.getCurrentTenantId();
        if (tenantId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Tenant no resuelto");
        }
        return tenantId;
    }

    private String extractLastDigits(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String digits = value.replaceAll("\\D", "");
        if (digits.isBlank()) {
            return null;
        }
        return digits.length() <= 4 ? digits : digits.substring(digits.length() - 4);
    }

    private String trimToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private TenantMetodoPagoResponseDTO mapToDTO(TenantMetodoPago tenantMetodoPago) {
        return new TenantMetodoPagoResponseDTO(
                tenantMetodoPago.getId(),
                tenantMetodoPago.getMetodoPago().getId(),
                tenantMetodoPago.getMetodoPago().getNombre(),
                tenantMetodoPago.getTitular(),
                tenantMetodoPago.getUltimosDigitos(),
                tenantMetodoPago.getMarca(),
                tenantMetodoPago.getReferencia(),
                tenantMetodoPago.getEsPrincipal(),
                tenantMetodoPago.getEstado(),
                tenantMetodoPago.getFechaCreacion(),
                tenantMetodoPago.getFechaActualizacion());
    }
}
