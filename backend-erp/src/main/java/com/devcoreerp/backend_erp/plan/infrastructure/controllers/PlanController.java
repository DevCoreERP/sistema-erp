package com.devcoreerp.backend_erp.plan.infrastructure.controllers;

import com.devcoreerp.backend_erp.auth.infrastructure.config.ApiConfig;
import com.devcoreerp.backend_erp.plan.application.services.PlanService;
import com.devcoreerp.backend_erp.plan.infrastructure.dtos.PlanResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiConfig.API_BASE_PATH + "/planes")
@Tag(name = "Planes", description = "Catalogo publico de planes, modulos y beneficios disponibles para tenants")
public class PlanController {

    private final PlanService planService;

    public PlanController(PlanService planService) {
        this.planService = planService;
    }

    @GetMapping
    @Operation(
            summary = "PLAN_LISTAR",
            description = "Lista los planes activos disponibles para contratacion. Incluye precio en USD, limite de usuarios, modulos y beneficios asociados.")
    public ResponseEntity<List<PlanResponseDTO>> listarPlanes() {
        return ResponseEntity.ok(planService.listarPlanesActivos());
    }

    @GetMapping("/{planId}")
    @Operation(
            summary = "PLAN_DETALLE",
            description = "Obtiene el detalle de un plan activo especifico, incluyendo sus modulos habilitados y beneficios para mostrar al cliente en la pantalla de planes.")
    public ResponseEntity<PlanResponseDTO> obtenerPlan(@PathVariable Long planId) {
        return ResponseEntity.ok(planService.obtenerPlanActivo(planId));
    }
}
