import { Injectable } from '@angular/core';
import { Empleado } from '../interface/empleado.interface';

@Injectable({
  providedIn: 'root',
})
export class EmpleadosService {
  private empleados: Empleado[] = [
    {
      id: 1,
      nombre: 'Juan Pérez',
      correo: 'juan.perez@empresa.com',
      puesto: 'Analista de RRHH',
      direccion: 'Santa Cruz, Bolivia',
      estado: 'Activo',
    },
    {
      id: 2,
      nombre: 'Ana López',
      correo: 'ana.lopez@empresa.com',
      puesto: 'Contadora',
      direccion: 'Montero, Bolivia',
      estado: 'Activo',
    },
    {
      id: 3,
      nombre: 'Carlos Roca',
      correo: 'carlos.roca@empresa.com',
      puesto: 'Jefe de Nómina',
      direccion: 'Warnes, Bolivia',
      estado: 'Inactivo',
    },
    {
      id: 4,
      nombre: 'María Suárez',
      correo: 'maria.suarez@empresa.com',
      puesto: 'Asistente Administrativa',
      direccion: 'Santa Cruz, Bolivia',
      estado: 'Activo',
    },
  ];

  private contadorId = 5;

  obtenerEmpleados(): Empleado[] {
    return this.empleados;
  }

  agregarEmpleado(data: Omit<Empleado, 'id'>) {
  const nuevo: Empleado = {
    ...data,
    id: this.contadorId++,
  };

  this.empleados.push(nuevo);
}
  obtenerEmpleadoPorId(id: number) {
    return this.empleados.find((e) => e.id === id);
  }

  actualizarEmpleado(id: number, data: Partial<Empleado>) {
    const index = this.empleados.findIndex((e) => e.id === id);

    if (index !== -1) {
      this.empleados[index] = {
        ...this.empleados[index],
        ...data,
      };
    }
  }
}