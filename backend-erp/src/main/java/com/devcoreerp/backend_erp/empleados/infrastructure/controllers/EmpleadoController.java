package com.devcoreerp.backend_erp.empleados.infrastructure.controllers;

import com.devcoreerp.backend_erp.auth.infrastructure.annotations.RequirePermission;
import com.devcoreerp.backend_erp.auth.infrastructure.config.ApiConfig;
import com.devcoreerp.backend_erp.empleados.infrastructure.dtos.CreateEmpleadoRequest;
import com.devcoreerp.backend_erp.empleados.infrastructure.dtos.CreateEmpleadoResponse;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * EmpleadoController: Maneja las operaciones relacionadas con empleados.
 * 
 * Endpoints:
 * - POST /empleados - Crear empleado (requiere permiso EMPLOYEE_CREATE)
 */
@RestController
@RequestMapping(ApiConfig.API_BASE_PATH + "/empleados")
public class EmpleadoController {

    private static final Logger logger = LogManager.getLogger(EmpleadoController.class);

    private final com.devcoreerp.backend_erp.empleados.application.services.CreateEmpleadoUseCase createEmpleadoUseCase;
    private final com.devcoreerp.backend_erp.empleados.application.services.GetEmpleadosUseCase getEmpleadosUseCase;

    public EmpleadoController(
            com.devcoreerp.backend_erp.empleados.application.services.CreateEmpleadoUseCase createEmpleadoUseCase,
            com.devcoreerp.backend_erp.empleados.application.services.GetEmpleadosUseCase getEmpleadosUseCase) {

        this.createEmpleadoUseCase = createEmpleadoUseCase;
        this.getEmpleadosUseCase = getEmpleadosUseCase;
    }

    /**
     * Endpoint para crear un nuevo empleado
     * POST /hey-fincas-api/v1/empleados
     * 
     * Este endpoint maneja la creación transaccional de:
     * 1. PERSONA (obligatorio)
     * 2. EMPLEADO (obligatorio)
     * 3. USUARIO (opcional - si se envían credenciales)
     * 
     * Requiere permiso: EMPLOYEE_CREATE
     * 
     * @param request DTO con datos de Persona, Empleado y Usuario (opcional)
     * @return CreateEmpleadoResponse con los datos creados
     */
    @PostMapping
    @RequirePermission(value = "USER_CREATE", description = "Permiso para crear empleados") // correguir bugs
    public ResponseEntity<?> createEmpleado(
            @RequestBody @Valid CreateEmpleadoRequest request) {

        logger.info("[EMPLEADO] Request para crear empleado: {}", request.codigoEmpleado());

        try {
            // Ejecuta el caso de uso (toda la lógica es transaccional)
            CreateEmpleadoResponse response = createEmpleadoUseCase.execute(request);

            logger.info("[EMPLEADO] Empleado creado exitosamente: {}",
                    response.empleado().codigoEmpleado());

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(response);

        } catch (IllegalStateException e) {
            // Errores de validación de negocio
            logger.warn("[EMPLEADO] Error de validación: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));

        } catch (Exception e) {
            // Errores inesperados
            logger.error("[EMPLEADO] Error inesperado al crear empleado: {}", e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error interno del servidor"));
        }
    }

    /**
     * DTO simple para respuestas de error
     */
    private record ErrorResponse(String error) {
    }

    /**
     * Para listar los empleados
     * 
     * @return
     */
    @GetMapping
    @RequirePermission(value = "USER_VIEW", description = "Permiso para listar empleados")
    public ResponseEntity<?> getEmpleados() {

        logger.info("[EMPLEADO] Listando empleados");

        try {
            var empleados = getEmpleadosUseCase.execute();

            return ResponseEntity.ok(empleados);

        } catch (Exception e) {
            logger.error("[EMPLEADO] Error al listar empleados: {}", e.getMessage(), e);

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error interno del servidor"));
        }
    }

}