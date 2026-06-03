package com.devcoreerp.backend_erp.subcripcion.application.services;

import com.devcoreerp.backend_erp.auth.application.services.EffectivePermissionService;
import com.devcoreerp.backend_erp.multitenancy.Tenant;
import com.devcoreerp.backend_erp.multitenancy.TenantContext;
import com.devcoreerp.backend_erp.multitenancy.TenantRepository;
import com.devcoreerp.backend_erp.multitenancy.TenantStatus;
import com.devcoreerp.backend_erp.pago.domain.Pago;
import com.devcoreerp.backend_erp.pago.domain.PagoEstado;
import com.devcoreerp.backend_erp.pago.domain.TenantMetodoPago;
import com.devcoreerp.backend_erp.pago.infrastructure.persistance.PagoRepository;
import com.devcoreerp.backend_erp.pago.infrastructure.persistance.TenantMetodoPagoRepository;
import com.devcoreerp.backend_erp.plan.application.services.PlanService;
import com.devcoreerp.backend_erp.plan.domain.Plan;
import com.devcoreerp.backend_erp.plan.infrastructure.persistance.PlanRepository;
import com.devcoreerp.backend_erp.subcripcion.domain.Suscripcion;
import com.devcoreerp.backend_erp.subcripcion.domain.SuscripcionEstado;
import com.devcoreerp.backend_erp.subcripcion.domain.SuscripcionTipo;
import com.devcoreerp.backend_erp.subcripcion.infrastructure.dtos.PlanAccessResponseDTO;
import com.devcoreerp.backend_erp.subcripcion.infrastructure.dtos.SuscripcionCompraRequestDTO;
import com.devcoreerp.backend_erp.subcripcion.infrastructure.dtos.SuscripcionResponseDTO;
import com.devcoreerp.backend_erp.subcripcion.infrastructure.persistance.SuscripcionRepository;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class SuscripcionService {

    private final TenantRepository tenantRepository;
    private final PlanRepository planRepository;
    private final TenantMetodoPagoRepository tenantMetodoPagoRepository;
    private final SuscripcionRepository suscripcionRepository;
    private final PagoRepository pagoRepository;
    private final EffectivePermissionService effectivePermissionService;
    private final PlanService planService;

    public SuscripcionService(
            TenantRepository tenantRepository,
            PlanRepository planRepository,
            TenantMetodoPagoRepository tenantMetodoPagoRepository,
            SuscripcionRepository suscripcionRepository,
            PagoRepository pagoRepository,
            EffectivePermissionService effectivePermissionService,
            PlanService planService) {
        this.tenantRepository = tenantRepository;
        this.planRepository = planRepository;
        this.tenantMetodoPagoRepository = tenantMetodoPagoRepository;
        this.suscripcionRepository = suscripcionRepository;
        this.pagoRepository = pagoRepository;
        this.effectivePermissionService = effectivePermissionService;
        this.planService = planService;
    }

    public void crearSuscripcionPrueba(Tenant tenant) {
        if (tenant == null || tenant.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tenant requerido para crear prueba");
        }

        if (suscripcionRepository.findFirstByTenant_IdOrderByFechaCreacionDesc(tenant.getId()).isPresent()) {
            return;
        }

        Plan planGratis = planRepository.findByNombreAndEstadoTrue(Plan.FREE_TRIAL_PLAN_NAME)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plan Gratis / Prueba no encontrado"));

        LocalDate fechaInicio = tenant.getFechaInicioPrueba() != null ? tenant.getFechaInicioPrueba() : LocalDate.now();
        LocalDate fechaFin = tenant.getFechaFinPrueba() != null ? tenant.getFechaFinPrueba() : fechaInicio.plusDays(7);
        tenant.setFechaInicioPrueba(fechaInicio);
        tenant.setFechaFinPrueba(fechaFin);

        Suscripcion suscripcion = Suscripcion.builder()
                .tenant(tenant)
                .plan(planGratis)
                .fechaInicio(fechaInicio)
                .fechaFin(fechaFin)
                .estado(SuscripcionEstado.PRUEBA)
                .tipo(SuscripcionTipo.PRUEBA)
                .fechaProximoVencimiento(fechaFin)
                .planNombreSnapshot(planGratis.getNombre())
                .precioUsdSnapshot(planGratis.getPrecioUsd())
                .limiteUsuariosSnapshot(planGratis.getLimiteUsuarios())
                .build();

        suscripcionRepository.save(suscripcion);
    }

    @Transactional(readOnly = true)
    public SuscripcionResponseDTO obtenerActual() {
        Long tenantId = requireTenantId();
        LocalDate today = LocalDate.now();
        Optional<Suscripcion> vigente = findCurrentSubscription(tenantId, today);
        Suscripcion suscripcion = vigente.orElseGet(() -> suscripcionRepository
                .findFirstByTenant_IdOrderByFechaCreacionDesc(tenantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El tenant no tiene suscripciones")));

        return mapToDTO(suscripcion, suscripcion.estaVigente(today)
                && (suscripcion.getEstado() == SuscripcionEstado.ACTIVA || suscripcion.getEstado() == SuscripcionEstado.PRUEBA));
    }

    @Transactional(readOnly = true)
    public PlanAccessResponseDTO obtenerAccesosActuales() {
        Long tenantId = requireTenantId();
        Set<String> allowedCodes = effectivePermissionService.getAllowedPermissionCodesForTenant(tenantId)
                .stream()
                .sorted()
                .collect(Collectors.toCollection(java.util.LinkedHashSet::new));

        return new PlanAccessResponseDTO(
                effectivePermissionService.findCurrentPlanForTenant(tenantId)
                        .map(planService::mapToDTO)
                        .orElse(null),
                allowedCodes);
    }

    public SuscripcionResponseDTO adquirirPlan(SuscripcionCompraRequestDTO dto) {
        Long tenantId = requireTenantId();
        LocalDate today = LocalDate.now();
        Tenant tenant = getTenant(tenantId);
        Plan plan = planRepository.findByIdAndEstadoTrue(dto.planId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plan no encontrado o inactivo"));

        if (Plan.FREE_TRIAL_PLAN_NAME.equalsIgnoreCase(plan.getNombre()) || plan.getPrecioUsd().signum() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El plan Gratis / Prueba no se puede comprar");
        }

        expirarSuscripcionesVencidas(tenantId, today);

        Optional<Suscripcion> currentSub = findCurrentSubscription(tenantId, today);
        if (currentSub.isPresent()) {
            Suscripcion oldSub = currentSub.get();
            oldSub.setEstado(SuscripcionEstado.VENCIDA);
            oldSub.setFechaFin(today);
            suscripcionRepository.save(oldSub);
        }

        TenantMetodoPago tenantMetodoPago = tenantMetodoPagoRepository
                .findByIdAndTenant_Id(dto.tenantMetodoPagoId(), tenantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Metodo de pago del tenant no encontrado"));

        if (!Boolean.TRUE.equals(tenantMetodoPago.getEstado())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Metodo de pago del tenant inactivo");
        }

        LocalDate fechaFin = today.plusMonths(1);
        Suscripcion suscripcion = Suscripcion.builder()
                .tenant(tenant)
                .plan(plan)
                .tenantMetodoPago(tenantMetodoPago)
                .fechaInicio(today)
                .fechaFin(fechaFin)
                .estado(SuscripcionEstado.ACTIVA)
                .tipo(SuscripcionTipo.PAGADA)
                .fechaProximoVencimiento(fechaFin)
                .planNombreSnapshot(plan.getNombre())
                .precioUsdSnapshot(plan.getPrecioUsd())
                .limiteUsuariosSnapshot(plan.getLimiteUsuarios())
                .build();

        Suscripcion savedSuscripcion = suscripcionRepository.save(suscripcion);

        Pago pago = Pago.builder()
                .tenant(tenant)
                .suscripcion(savedSuscripcion)
                .tenantMetodoPago(tenantMetodoPago)
                .estado(PagoEstado.PAGADO)
                .montoPagadoUsd(plan.getPrecioUsd())
                .observacion("Pago simulado de suscripcion " + plan.getNombre())
                .build();
        pagoRepository.save(pago);

        if (tenant.getStatus() != TenantStatus.ACTIVE) {
            tenant.setStatus(TenantStatus.ACTIVE);
            tenantRepository.save(tenant);
        }

        return mapToDTO(savedSuscripcion, true);
    }

    private Optional<Suscripcion> findCurrentSubscription(Long tenantId, LocalDate today) {
        Optional<Suscripcion> active = suscripcionRepository
                .findFirstByTenant_IdAndEstadoAndFechaInicioLessThanEqualAndFechaFinGreaterThanEqualOrderByFechaFinDesc(
                        tenantId,
                        SuscripcionEstado.ACTIVA,
                        today,
                        today);
        if (active.isPresent()) {
            return active;
        }

        return suscripcionRepository
                .findFirstByTenant_IdAndEstadoAndFechaInicioLessThanEqualAndFechaFinGreaterThanEqualOrderByFechaFinDesc(
                        tenantId,
                        SuscripcionEstado.PRUEBA,
                        today,
                        today);
    }

    private void expirarSuscripcionesVencidas(Long tenantId, LocalDate today) {
        List<Suscripcion> abiertas = suscripcionRepository.findByTenant_IdAndEstadoIn(
                tenantId,
                List.of(SuscripcionEstado.ACTIVA, SuscripcionEstado.PRUEBA));

        abiertas.stream()
                .filter(suscripcion -> suscripcion.getFechaFin().isBefore(today))
                .forEach(suscripcion -> {
                    suscripcion.setEstado(SuscripcionEstado.VENCIDA);
                    suscripcionRepository.save(suscripcion);
                });
        suscripcionRepository.flush();
    }

    private Tenant getTenant(Long tenantId) {
        return tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tenant no encontrado"));
    }

    private Long requireTenantId() {
        Long tenantId = TenantContext.getCurrentTenantId();
        if (tenantId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Tenant no resuelto");
        }
        return tenantId;
    }

    private SuscripcionResponseDTO mapToDTO(Suscripcion suscripcion, boolean vigente) {
        return new SuscripcionResponseDTO(
                suscripcion.getId(),
                suscripcion.getTenant().getId(),
                suscripcion.getPlan().getId(),
                suscripcion.getPlan().getNombre(),
                suscripcion.getTenantMetodoPago() == null ? null : suscripcion.getTenantMetodoPago().getId(),
                suscripcion.getEstado(),
                suscripcion.getTipo(),
                suscripcion.getFechaInicio(),
                suscripcion.getFechaFin(),
                suscripcion.getFechaProximoVencimiento(),
                suscripcion.getPlanNombreSnapshot(),
                suscripcion.getPrecioUsdSnapshot(),
                suscripcion.getLimiteUsuariosSnapshot(),
                vigente,
                suscripcion.getFechaCreacion(),
                suscripcion.getFechaActualizacion());
    }
}
