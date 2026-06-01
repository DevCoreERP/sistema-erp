package com.devcoreerp.backend_erp.plan.infrastructure.controllers;

import com.devcoreerp.backend_erp.auth.infrastructure.config.ApiConfig;
import com.devcoreerp.backend_erp.plan.application.services.PlanService;
import com.devcoreerp.backend_erp.plan.infrastructure.dtos.PlanResponseDTO;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiConfig.API_BASE_PATH + "/planes")
public class PlanController {

    private final PlanService planService;

    public PlanController(PlanService planService) {
        this.planService = planService;
    }

    @GetMapping
    public ResponseEntity<List<PlanResponseDTO>> listarPlanes() {
        return ResponseEntity.ok(planService.listarPlanesActivos());
    }

    @GetMapping("/{planId}")
    public ResponseEntity<PlanResponseDTO> obtenerPlan(@PathVariable Long planId) {
        return ResponseEntity.ok(planService.obtenerPlanActivo(planId));
    }
}
