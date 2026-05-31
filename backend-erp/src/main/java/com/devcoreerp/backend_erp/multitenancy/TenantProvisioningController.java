package com.devcoreerp.backend_erp.multitenancy;

import com.devcoreerp.backend_erp.auth.infrastructure.config.ApiConfig;
import com.devcoreerp.backend_erp.multitenancy.dtos.TenantProvisioningRequestDTO;
import com.devcoreerp.backend_erp.multitenancy.dtos.TenantResponseDTO;
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
    public ResponseEntity<TenantResponseDTO> provisionTenant(
            @RequestHeader(name = TenantConstants.PROVISIONING_HEADER, required = false) String providedKey,
            @RequestBody @Valid TenantProvisioningRequestDTO request) {
        if (provisioningKey == null || provisioningKey.isBlank() || !provisioningKey.equals(providedKey)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Provisioning key invalida");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(tenantProvisioningService.provisionTenant(request));
    }
}
