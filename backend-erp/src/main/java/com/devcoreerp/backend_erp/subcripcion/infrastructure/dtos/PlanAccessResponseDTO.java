package com.devcoreerp.backend_erp.subcripcion.infrastructure.dtos;

import com.devcoreerp.backend_erp.plan.infrastructure.dtos.PlanResponseDTO;
import java.util.Set;

public record PlanAccessResponseDTO(
        PlanResponseDTO plan,
        Set<String> permisosHabilitados
) {
}
