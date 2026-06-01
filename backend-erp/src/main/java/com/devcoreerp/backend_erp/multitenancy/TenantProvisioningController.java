package com.devcoreerp.backend_erp.multitenancy;

import com.devcoreerp.backend_erp.auth.infrastructure.config.ApiConfig;
import com.devcoreerp.backend_erp.multitenancy.dtos.TenantProvisioningRequestDTO;
import com.devcoreerp.backend_erp.multitenancy.dtos.TenantResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(ApiConfig.API_BASE_PATH + "/tenants")
@Tag(name = "Multitenancy", description = "Provisionamiento y administracion inicial de tenants SaaS")
public class TenantProvisioningController {

    private final TenantProvisioningService tenantProvisioningService;
    private final String provisioningKey;

    public TenantProvisioningController(
            TenantProvisioningService tenantProvisioningService,
            @Value("${application.multitenancy.provisioning-key}") String provisioningKey) {
        this.tenantProvisioningService = tenantProvisioningService;
        this.provisioningKey = provisioningKey;
    }

    @PostMapping
    @Operation(
            summary = "TENANT_CREAR",
            description = "Crea un nuevo tenant SaaS. Requiere el header de provisioning, registra el tenant en public, crea su schema, ejecuta migraciones tenant, crea roles base, usuario admin inicial y asigna prueba gratuita de 7 dias.")
    public ResponseEntity<TenantResponseDTO> provisionTenant(
            @RequestHeader(name = TenantConstants.PROVISIONING_HEADER, required = false) String providedKey,
            @RequestBody @Valid TenantProvisioningRequestDTO request) {
        if (provisioningKey == null || provisioningKey.isBlank() || !provisioningKey.equals(providedKey)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Provisioning key invalida");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(tenantProvisioningService.provisionTenant(request));
    }
}
