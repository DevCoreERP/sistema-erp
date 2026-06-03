package com.devcoreerp.backend_erp.plan.application.services;

import com.devcoreerp.backend_erp.plan.domain.Beneficio;
import com.devcoreerp.backend_erp.plan.domain.Modulo;
import com.devcoreerp.backend_erp.plan.domain.Plan;
import com.devcoreerp.backend_erp.plan.infrastructure.dtos.BeneficioResponseDTO;
import com.devcoreerp.backend_erp.plan.infrastructure.dtos.ModuloResponseDTO;
import com.devcoreerp.backend_erp.plan.infrastructure.dtos.PlanResponseDTO;
import com.devcoreerp.backend_erp.plan.infrastructure.persistance.PlanRepository;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class PlanService {

    private final PlanRepository planRepository;

    public PlanService(PlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    public List<PlanResponseDTO> listarPlanesActivos() {
        return planRepository.findByEstadoTrueOrderByPrecioUsdAsc()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    public PlanResponseDTO obtenerPlanActivo(Long planId) {
        Plan plan = planRepository.findByIdAndEstadoTrue(planId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plan no encontrado o inactivo"));
        return mapToDTO(plan);
    }

    public PlanResponseDTO mapToDTO(Plan plan) {
        return new PlanResponseDTO(
                plan.getId(),
                plan.getNombre(),
                plan.getDescripcion(),
                plan.getPrecioUsd(),
                plan.getLimiteUsuarios(),
                plan.getEstado(),
                plan.getFechaCreacion(),
                plan.getFechaActualizacion(),
                mapModulos(plan.getModulos()),
                mapBeneficios(plan.getBeneficios()));
    }

    private Set<ModuloResponseDTO> mapModulos(Set<Modulo> modulos) {
        return modulos.stream()
                .filter(modulo -> Boolean.TRUE.equals(modulo.getEstado()))
                .sorted(Comparator.comparing(Modulo::getNombre))
                .map(modulo -> new ModuloResponseDTO(
                        modulo.getId(),
                        modulo.getNombre(),
                        modulo.getDescripcion(),
                        modulo.getEstado()))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private Set<BeneficioResponseDTO> mapBeneficios(Set<Beneficio> beneficios) {
        return beneficios.stream()
                .filter(beneficio -> Boolean.TRUE.equals(beneficio.getEstado()))
                .sorted(Comparator.comparing(Beneficio::getNombre))
                .map(beneficio -> new BeneficioResponseDTO(
                        beneficio.getId(),
                        beneficio.getNombre(),
                        beneficio.getDescripcion(),
                        beneficio.getEstado()))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
