package com.devcoreerp.backend_erp.vacaciones.application.mappers;

import com.devcoreerp.backend_erp.vacaciones.infrastructure.dtos.CreateVacacionDTO;
import com.devcoreerp.backend_erp.vacaciones.infrastructure.dtos.ResponseVacacionDTO;

import com.devcoreerp.backend_erp.empleados.domain.Empleado;
import com.devcoreerp.backend_erp.vacaciones.domain.Vacacion;
import com.devcoreerp.backend_erp.empleados.infrastructure.dtos.EmpleadoResponse;

public class VacacionMapper {

    public static ResponseVacacionDTO toDTO(Vacacion vacacion) {
        if (vacacion == null) return null;

        Empleado empleado = vacacion.getEmpleado();

        return new ResponseVacacionDTO(
            vacacion.getId(),
            vacacion.getDias(),
            vacacion.getCreatedAt(),
            vacacion.getUpdatedAt(),
            new EmpleadoResponse(
                empleado.getId(),
                empleado.getCodigoEmpleado(),
                empleado.getPersona().getFirstName(),
                empleado.getPersona().getSurnames(),
                empleado.getPersona().getEmail()
            )
        );
    }

    public static Vacacion toEntity(CreateVacacionDTO dto, Empleado empleado) {
        if (dto == null) return null;

        Vacacion vacacion = new Vacacion(empleado, dto.dias());
        return vacacion;
    }
}