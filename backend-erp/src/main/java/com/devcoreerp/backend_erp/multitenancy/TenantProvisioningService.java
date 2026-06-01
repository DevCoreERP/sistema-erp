package com.devcoreerp.backend_erp.multitenancy;

import com.devcoreerp.backend_erp.auth.domain.Role;
import com.devcoreerp.backend_erp.auth.domain.Usuario;
import com.devcoreerp.backend_erp.auth.infrastructure.persistance.RoleRepository;
import com.devcoreerp.backend_erp.auth.infrastructure.persistance.UsuarioRepository;
import com.devcoreerp.backend_erp.config.BasePermissionSeeder;
import com.devcoreerp.backend_erp.config.BaseRoleSeeder;
import com.devcoreerp.backend_erp.multitenancy.dtos.TenantProvisioningRequestDTO;
import com.devcoreerp.backend_erp.multitenancy.dtos.TenantResponseDTO;
import com.devcoreerp.backend_erp.multitenancy.exceptions.InvalidTenantException;
import com.devcoreerp.backend_erp.multitenancy.exceptions.TenantProvisioningException;
import com.devcoreerp.backend_erp.subcripcion.application.services.SuscripcionService;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TenantProvisioningService {

    private final TenantRepository tenantRepository;
    private final DataSource dataSource;
    private final BasePermissionSeeder basePermissionSeeder;
    private final BaseRoleSeeder baseRoleSeeder;
    private final UsuarioRepository usuarioRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final TransactionTemplate transactionTemplate;
    private final SuscripcionService suscripcionService;

    public TenantProvisioningService(
            TenantRepository tenantRepository,
            DataSource dataSource,
            BasePermissionSeeder basePermissionSeeder,
            BaseRoleSeeder baseRoleSeeder,
            UsuarioRepository usuarioRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            TransactionTemplate transactionTemplate,
            SuscripcionService suscripcionService) {
        this.tenantRepository = tenantRepository;
        this.dataSource = dataSource;
        this.basePermissionSeeder = basePermissionSeeder;
        this.baseRoleSeeder = baseRoleSeeder;
        this.usuarioRepository = usuarioRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.transactionTemplate = transactionTemplate;
        this.suscripcionService = suscripcionService;
    }

    public TenantResponseDTO provisionTenant(TenantProvisioningRequestDTO request) {
        String subdomain = TenantValidator.normalizeSubdomain(request.subdomain());
        String schemaName = TenantValidator.schemaNameForSubdomain(subdomain);
        LocalDate fechaInicioPrueba = LocalDate.now();
        LocalDate fechaFinPrueba = fechaInicioPrueba.plusDays(7);
        validateTenantDoesNotExist(subdomain, schemaName);

        Tenant tenant = Tenant.builder()
                .name(request.name().trim())
                .subdomain(subdomain)
                .schemaName(schemaName)
                .status(TenantStatus.INACTIVE)
                .fechaInicioPrueba(fechaInicioPrueba)
                .fechaFinPrueba(fechaFinPrueba)
                .build();

        try {
            tenant = tenantRepository.saveAndFlush(tenant);
        } catch (DataIntegrityViolationException exception) {
            throw new InvalidTenantException("Ya existe un tenant con ese subdominio o schema");
        }

        try {
            TenantContext.clear();
            basePermissionSeeder.seedBasePermissions();
            createSchema(schemaName);
            migrateTenantSchema(schemaName);
            initializeTenantData(tenant, request);
            suscripcionService.crearSuscripcionPrueba(tenant);
            tenant.setStatus(TenantStatus.ACTIVE);
            return toResponseDTO(tenantRepository.save(tenant));
        } catch (Exception exception) {
            markTenantInactive(tenant.getId());
            throw new TenantProvisioningException("No se pudo provisionar el tenant: " + subdomain, exception);
        } finally {
            TenantContext.clear();
        }
    }

    private void validateTenantDoesNotExist(String subdomain, String schemaName) {
        if (tenantRepository.existsBySubdomain(subdomain)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un tenant con subdominio: " + subdomain);
        }
        if (tenantRepository.existsBySchemaName(schemaName)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un tenant con schema: " + schemaName);
        }
    }

    private void createSchema(String schemaName) {
        TenantValidator.validateSchemaName(schemaName);
        try (Connection connection = dataSource.getConnection();
                Statement statement = connection.createStatement()) {
            statement.execute("CREATE SCHEMA IF NOT EXISTS " + TenantValidator.quotedSchemaName(schemaName));
        } catch (SQLException exception) {
            throw new TenantProvisioningException("Error al crear el schema del tenant", exception);
        }
    }

    private void migrateTenantSchema(String schemaName) {
        TenantValidator.validateSchemaName(schemaName);
        Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration/tenant")
                .schemas(schemaName)
                .defaultSchema(schemaName)
                .createSchemas(false)
                .baselineOnMigrate(true)
                .baselineVersion("0")
                .load()
                .migrate();
    }

    private void initializeTenantData(Tenant tenant, TenantProvisioningRequestDTO request) {
        TenantContext.setCurrentTenant(tenant);
        try {
            transactionTemplate.execute(status -> {
                baseRoleSeeder.initializeBaseRolesForCurrentTenant();
                createInitialAdminUser(request);
                return null;
            });
        } finally {
            TenantContext.clear();
        }
    }

    private void createInitialAdminUser(TenantProvisioningRequestDTO request) {
        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new TenantProvisioningException("Rol ADMIN no fue creado para el tenant"));

        Usuario usuario = Usuario.builder()
                .username(request.adminUsername().trim())
                .password(passwordEncoder.encode(request.adminPassword()))
                .email(request.adminEmail().trim().toLowerCase())
                .firstName(request.adminFirstName().trim())
                .surnames(request.adminSurnames().trim())
                .phoneNumber(request.adminPhoneNumber().trim())
                .fechaIngreso(LocalDate.now())
                .estado(true)
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .build();
        usuario.getRoles().add(adminRole);
        usuarioRepository.save(usuario);
    }

    private void markTenantInactive(Long tenantId) {
        if (tenantId == null) {
            return;
        }
        TenantContext.clear();
        tenantRepository.findById(tenantId).ifPresent(tenant -> {
            tenant.setStatus(TenantStatus.INACTIVE);
            tenantRepository.save(tenant);
        });
    }

    private TenantResponseDTO toResponseDTO(Tenant tenant) {
        return new TenantResponseDTO(
                tenant.getId(),
                tenant.getName(),
                tenant.getSubdomain(),
                tenant.getSchemaName(),
                tenant.getStatus(),
                tenant.getCreatedAt(),
                tenant.getUpdatedAt(),
                tenant.getFechaInicioPrueba(),
                tenant.getFechaFinPrueba());
    }
}
