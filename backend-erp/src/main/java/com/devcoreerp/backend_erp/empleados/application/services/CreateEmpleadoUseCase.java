package com.devcoreerp.backend_erp.empleados.application.services;

import com.devcoreerp.backend_erp.auth.domain.Persona;
import com.devcoreerp.backend_erp.auth.domain.Usuario;
import com.devcoreerp.backend_erp.auth.infrastructure.dtos.CreateUsuarioDTO;
import com.devcoreerp.backend_erp.empleados.domain.Empleado;
import com.devcoreerp.backend_erp.empleados.infrastructure.dtos.CreateEmpleadoRequest;
import com.devcoreerp.backend_erp.empleados.infrastructure.dtos.CreateEmpleadoResponse;
import com.devcoreerp.backend_erp.empleados.infrastructure.persistance.EmpleadoRepository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Caso de uso: Crear Empleado
 * 
 * Flujo transaccional:
 * 1. Validar que no exista persona con mismo email/documento
 * 2. Validar que no exista empleado con mismo código
 * 3. Crear Persona
 * 4. Crear Empleado vinculado a Persona
 * 5. Opcionalmente crear Usuario (si se envían credenciales)
 * 6. Si cualquier paso falla, hacer rollback completo
 * 
 * Anotado con @Transactional para garantizar atomicidad
 */
@Service
public class CreateEmpleadoUseCase {
    
    private static final Logger logger = LogManager.getLogger(CreateEmpleadoUseCase.class);
    
    private final com.devcoreerp.backend_erp.auth.infrastructure.persistance.PersonaRepository personaRepository;
    private final EmpleadoRepository empleadoRepository;
    private final com.devcoreerp.backend_erp.auth.domain.services.AuthService authService;
    
    public CreateEmpleadoUseCase(
            com.devcoreerp.backend_erp.auth.infrastructure.persistance.PersonaRepository personaRepository,
            EmpleadoRepository empleadoRepository,
            com.devcoreerp.backend_erp.auth.domain.services.AuthService authService) {
        this.personaRepository = personaRepository;
        this.empleadoRepository = empleadoRepository;
        this.authService = authService;
    }
    
    /**
     * Ejecuta el caso de uso de creación de empleado.
     * Toda la operación es atómica gracias a @Transactional.
     * 
     * @param request DTO con datos de Persona, Empleado y Usuario (opcional)
     * @return CreateEmpleadoResponse con los datos creados
     * @throws IllegalStateException si hay validaciones de negocio que fallan
     */
    @Transactional(rollbackFor = Exception.class)
    public CreateEmpleadoResponse execute(CreateEmpleadoRequest request) {
        
        logger.info("[EMPLEADO] Iniciando creación de empleado: {}", request.codigoEmpleado());
        
        // ========== PASO 1: VALIDACIONES DE NEGOCIO ==========
        validateBusinessRules(request);
        
        // ========== PASO 2: CREAR PERSONA ==========
        Persona persona = createPersona(request);
        logger.info("[EMPLEADO] Persona creada con ID: {}", persona.getId());
        
        // ========== PASO 3: CREAR EMPLEADO ==========
        Empleado empleado = createEmpleado(request, persona);
        logger.info("[EMPLEADO] Empleado creado con ID: {}", empleado.getId());
        
        // ========== PASO 4: CREAR USUARIO (OPCIONAL) ==========
        Usuario usuario = null;
        /*if (request.shouldCreateUsuario()) {
            //usuario = createUsuario(request, persona);
            //logger.info("[EMPLEADO] Usuario creado con ID: {}", usuario.getId());
        }*/
        
        // ========== PASO 5: CONSTRUIR RESPUESTA ==========
        CreateEmpleadoResponse response = buildResponse(empleado, persona, usuario);
        
        logger.info("[EMPLEADO] Empleado creado exitosamente: {}", empleado.getCodigoEmpleado());
        
        return response;
    }
    
    /**
     * Validaciones de negocio antes de crear entidades
     */
    private void validateBusinessRules(CreateEmpleadoRequest request) {
        
        // Validar que no exista persona con mismo email
        if (personaRepository.existsByEmail(request.email())) {
            throw new IllegalStateException(
                "Ya existe una persona registrada con el email: " + request.email()
            );
        }
        
        // Validar que no exista persona con mismo documento
        if (personaRepository.existsByDocumentNumber(request.documentNumber())) {
            throw new IllegalStateException(
                "Ya existe una persona registrada con el documento: " + request.documentNumber()
            );
        }
        
        // Validar que no exista empleado con mismo código
        if (empleadoRepository.existsByCodigoEmpleado(request.codigoEmpleado())) {
            throw new IllegalStateException(
                "Ya existe un empleado con el código: " + request.codigoEmpleado()
            );
        }
        
        logger.info("[EMPLEADO] Validaciones de negocio pasadas correctamente");
    }
    
    /**
     * Crea y persiste la entidad Persona
     */
    private Persona createPersona(CreateEmpleadoRequest request) {
        
        Persona persona = Persona.builder()
            .firstName(request.firstName())
            .surnames(request.surnames())
            .documentType(request.documentType())
            .documentNumber(request.documentNumber())
            .email(request.email())
            .phoneNumber(request.phoneNumber())
            .address(request.address())
            .birthDate(request.birthDate())
            .active(true)
            .createdAt(new Date())
            .build();
        
        return personaRepository.save(persona);
    }
    
    /**
     * Crea y persiste la entidad Empleado vinculada a Persona
     */
    private Empleado createEmpleado(CreateEmpleadoRequest request, Persona persona) {
        
        Date fechaIngreso = request.fechaIngreso() != null 
            ? request.fechaIngreso() 
            : new Date();
        
        Empleado empleado = Empleado.builder()
            .persona(persona)
            .codigoEmpleado(request.codigoEmpleado())
            .fechaIngreso(fechaIngreso)
            .estado(true)
            .createdAt(new Date())
            .build();
        
        return empleadoRepository.save(empleado);
    }
    
    /**
     * Crea y persiste la entidad Usuario (opcional)
     * Delega en el servicio de autenticación existente
     */
    /*private Usuario createUsuario(CreateEmpleadoRequest request, Persona persona) {
        
        CreateUsuarioDTO createUsuarioDTO = new CreateUsuarioDTO(
            request.usuarioCredentials().username(),
            request.usuarioCredentials().password(),
            persona.getId(),
            request.usuarioCredentials().roleId()
        );
        
        // Delega en el servicio de autenticación existente
        // Este método ya existe en tu AuthService
        authService.createUsuario(createUsuarioDTO);
        
        // Retorna el usuario recién creado
        // Asumiendo que hay un método para obtener usuario por persona
        return persona.getUsuario();
    }*/
    
    /**
     * Construye la respuesta con los datos creados
     */
    private CreateEmpleadoResponse buildResponse(
            Empleado empleado, 
            Persona persona, 
            Usuario usuario) {
        
        // Datos del Empleado
        CreateEmpleadoResponse.EmpleadoData empleadoData = 
            new CreateEmpleadoResponse.EmpleadoData(
                empleado.getId(),
                empleado.getCodigoEmpleado(),
                empleado.getFechaIngreso(),
                empleado.getEstado(),
                empleado.getCreatedAt()
            );
        
        // Datos de la Persona
        CreateEmpleadoResponse.PersonaData personaData = 
            new CreateEmpleadoResponse.PersonaData(
                persona.getId(),
                persona.getFirstName(),
                persona.getSurnames(),
                persona.getFullName(),
                persona.getDocumentType(),
                persona.getDocumentNumber(),
                persona.getEmail(),
                persona.getPhoneNumber(),
                persona.getAddress(),
                persona.getBirthDate()
            );
        
        // Datos del Usuario (puede ser null)
        CreateEmpleadoResponse.UsuarioData usuarioData = null;
        if (usuario != null) {
            usuarioData = new CreateEmpleadoResponse.UsuarioData(
                usuario.getId(),
                usuario.getUsername(),
                usuario.getRole().getName(),
                usuario.getCreatedAt()
            );
        }
        
        return new CreateEmpleadoResponse(empleadoData, personaData, usuarioData);
    }
}