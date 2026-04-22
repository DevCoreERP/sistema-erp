package com.devcoreerp.backend_erp.empleados.application.services;

import com.devcoreerp.backend_erp.empleados.infrastructure.dtos.EmpleadoResponse;
import com.devcoreerp.backend_erp.empleados.infrastructure.persistance.EmpleadoRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetEmpleadosUseCase {

    private final com.devcoreerp.backend_erp.empleados.infrastructure.persistance.EmpleadoRepository empleadoRepository;

    public GetEmpleadosUseCase(EmpleadoRepository empleadoRepository) {
        this.empleadoRepository = empleadoRepository;
    }

    public List<EmpleadoResponse> execute() {
        return empleadoRepository.findAll()
                .stream()
                .map(e -> new EmpleadoResponse(
                        e.getId(),
                        e.getCodigoEmpleado(),
                        e.getPersona().getFirstName(),
                        e.getPersona().getSurnames(),
                        e.getPersona().getEmail()
                ))
                .toList();
    }
}