package com.devcoreerp.backend_erp.auth.application.services;

import com.devcoreerp.backend_erp.auth.domain.Usuario;
import com.devcoreerp.backend_erp.auth.infrastructure.persistance.PermissionRepository;
import com.devcoreerp.backend_erp.multitenancy.Tenant;
import com.devcoreerp.backend_erp.multitenancy.TenantContext;
import com.devcoreerp.backend_erp.multitenancy.TenantRepository;
import com.devcoreerp.backend_erp.plan.domain.Plan;
import com.devcoreerp.backend_erp.plan.infrastructure.persistance.PlanRepository;
import com.devcoreerp.backend_erp.subcripcion.domain.Suscripcion;
import com.devcoreerp.backend_erp.subcripcion.domain.SuscripcionEstado;
import com.devcoreerp.backend_erp.subcripcion.infrastructure.persistance.SuscripcionRepository;
import java.time.LocalDate;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class EffectivePermissionService {

    public static final Set<String> BILLING_PERMISSION_CODES = Set.of(
            "SUSCRIPCION_VER",
            "SUSCRIPCION_GESTIONAR",
            "METODO_PAGO_GESTIONAR",
            "PAGO_LISTAR");

    private final PermissionRepository permissionRepository;
    private final SuscripcionRepository suscripcionRepository;
    private final PlanRepository planRepository;
    private final TenantRepository tenantRepository;

    public EffectivePermissionService(
            PermissionRepository permissionRepository,
            SuscripcionRepository suscripcionRepository,
            PlanRepository planRepository,
            TenantRepository tenantRepository) {
        this.permissionRepository = permissionRepository;
        this.suscripcionRepository = suscripcionRepository;
        this.planRepository = planRepository;
        this.tenantRepository = tenantRepository;
    }

    public Collection<? extends GrantedAuthority> resolveEffectiveAuthorities(UserDetails userDetails) {
        Set<String> userPermissionCodes = resolveUserPermissionCodes(userDetails);
        Long tenantId = TenantContext.getCurrentTenantId();

        if (tenantId == null) {
            return toAuthorities(userPermissionCodes);
        }

        Set<String> allowedPermissionCodes = getAllowedPermissionCodesForTenant(tenantId);

        return toAuthorities(userPermissionCodes.stream()
                .filter(allowedPermissionCodes::contains)
                .collect(Collectors.toCollection(LinkedHashSet::new)));
    }

    public Set<String> getAllowedPermissionCodesForTenant(Long tenantId) {
        Set<String> allowedCodes = findCurrentPlanForTenant(tenantId)
                .map(plan -> permissionRepository.findActiveCodesAllowedByPlanId(plan.getId()))
                .orElseGet(Set::of)
                .stream()
                .map(String::toUpperCase)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        allowedCodes.addAll(BILLING_PERMISSION_CODES);
        return allowedCodes;
    }

    public Optional<Plan> findCurrentPlanForTenant(Long tenantId) {
        LocalDate today = LocalDate.now();

        Optional<Suscripcion> activeSubscription = suscripcionRepository
                .findFirstByTenant_IdAndEstadoAndFechaInicioLessThanEqualAndFechaFinGreaterThanEqualOrderByFechaFinDesc(
                        tenantId,
                        SuscripcionEstado.ACTIVA,
                        today,
                        today);

        if (activeSubscription.isPresent() && Boolean.TRUE.equals(activeSubscription.get().getPlan().getEstado())) {
            return Optional.of(activeSubscription.get().getPlan());
        }

        Optional<Suscripcion> trialSubscription = suscripcionRepository
                .findFirstByTenant_IdAndEstadoAndFechaInicioLessThanEqualAndFechaFinGreaterThanEqualOrderByFechaFinDesc(
                        tenantId,
                        SuscripcionEstado.PRUEBA,
                        today,
                        today);

        if (trialSubscription.isPresent() && Boolean.TRUE.equals(trialSubscription.get().getPlan().getEstado())) {
            return Optional.of(trialSubscription.get().getPlan());
        }

        return tenantRepository.findById(tenantId)
                .filter(tenant -> isTenantTrialActive(tenant, today))
                .flatMap(tenant -> planRepository.findByNombreAndEstadoTrue(Plan.FREE_TRIAL_PLAN_NAME));
    }

    private boolean isTenantTrialActive(Tenant tenant, LocalDate today) {
        return tenant.getFechaInicioPrueba() != null
                && tenant.getFechaFinPrueba() != null
                && !today.isBefore(tenant.getFechaInicioPrueba())
                && !today.isAfter(tenant.getFechaFinPrueba());
    }

    private Set<String> resolveUserPermissionCodes(UserDetails userDetails) {
        Set<String> permissions = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .map(String::toUpperCase)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        if (userDetails instanceof Usuario usuario && hasAdminRole(usuario)) {
            permissions.addAll(BILLING_PERMISSION_CODES);
        }

        return permissions;
    }

    private boolean hasAdminRole(Usuario usuario) {
        return usuario.getRoles() != null
                && usuario.getRoles().stream()
                        .anyMatch(role -> Boolean.TRUE.equals(role.getEstado())
                                && "ADMIN".equalsIgnoreCase(role.getName()));
    }

    private Set<GrantedAuthority> toAuthorities(Set<String> permissionCodes) {
        return permissionCodes.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
