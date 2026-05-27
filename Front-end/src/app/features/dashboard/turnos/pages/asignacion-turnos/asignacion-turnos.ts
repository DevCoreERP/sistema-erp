import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { Sidebar } from '../../../components/sidebar/sidebar';
import { Topbar } from '../../../components/topbar/topbar';

interface Empleado {
  id: number;
  nombre: string;
  correo: string;
  cargo: string;
  departamento: string;
  estado: 'Activo' | 'Inactivo';
}

interface Turno {
  id: number;
  nombre: string;
  horaInicio: string;
  horaFin: string;
  descripcion: string;
  estado: 'Activo' | 'Inactivo';
}

interface AsignacionTurno {
  id: number;
  empleadoId: number;
  empleado: string;
  departamento: string;
  turnoId: number;
  turno: string;
  horario: string;
  fechaI: string;
  fechaF: string;
  estado: 'Asignado' | 'Pendiente' | 'Conflicto';
}

@Component({
  selector: 'app-asignacion-turnos',
  standalone: true,
  imports: [CommonModule, FormsModule, Sidebar, Topbar],
  templateUrl: './asignacion-turnos.html',
  styleUrl: './asignacion-turnos.css',
})
export class AsignacionTurnos {
  busquedaEmpleado = '';

  empleadoSeleccionadoId: number | null = null;
  turnoSeleccionadoId: number | null = null;

  fechaI = '';
  fechaF = '';

  mensaje = '';
  tipoMensaje: 'ok' | 'error' | '' = '';

  empleados: Empleado[] = [ ];

  turnos: Turno[] = [ ];

  asignaciones: AsignacionTurno[] = [ ];

  get empleadosActivos(): Empleado[] {
    return this.empleados.filter((empleado) => empleado.estado === 'Activo');
  }

  get turnosActivos(): Turno[] {
    return this.turnos.filter((turno) => turno.estado === 'Activo');
  }

  get empleadosFiltrados(): Empleado[] {
    const texto = this.busquedaEmpleado.toLowerCase().trim();

    if (!texto) {
      return this.empleadosActivos;
    }

    return this.empleadosActivos.filter((empleado) =>
      empleado.nombre.toLowerCase().includes(texto) ||
      empleado.correo.toLowerCase().includes(texto) ||
      empleado.cargo.toLowerCase().includes(texto) ||
      empleado.departamento.toLowerCase().includes(texto)
    );
  }

  get totalAsignaciones(): number {
    return this.asignaciones.length;
  }

  get totalAsignadas(): number {
    return this.asignaciones.filter(
      (asignacion) => asignacion.estado === 'Asignado'
    ).length;
  }

  get totalPendientes(): number {
    return this.asignaciones.filter(
      (asignacion) => asignacion.estado === 'Pendiente'
    ).length;
  }

  get totalConflictos(): number {
    return this.asignaciones.filter(
      (asignacion) => asignacion.estado === 'Conflicto'
    ).length;
  }

  asignarTurno(): void {
    this.mensaje = '';
    this.tipoMensaje = '';

    if (
      !this.empleadoSeleccionadoId ||
      !this.turnoSeleccionadoId ||
      !this.fechaI ||
      !this.fechaF
    ) {
      this.mensaje = 'Debe completar todos los campos para asignar el turno.';
      this.tipoMensaje = 'error';
      return;
    }

    if (this.fechaF < this.fechaI) {
      this.mensaje = 'La fecha final no puede ser menor que la fecha inicial.';
      this.tipoMensaje = 'error';
      return;
    }

    const empleado = this.empleados.find(
      (item) => item.id === Number(this.empleadoSeleccionadoId)
    );

    const turno = this.turnos.find(
      (item) => item.id === Number(this.turnoSeleccionadoId)
    );

    if (!empleado || !turno) {
      this.mensaje = 'No se pudo encontrar el empleado o el turno seleccionado.';
      this.tipoMensaje = 'error';
      return;
    }

    const existeConflicto = this.asignaciones.some((asignacion) => {
      const mismoEmpleado = asignacion.empleadoId === empleado.id;
      const fechasCruzadas =
        this.fechaI <= asignacion.fechaF && this.fechaF >= asignacion.fechaI;

      return mismoEmpleado && fechasCruzadas;
    });

    if (existeConflicto) {
      this.mensaje =
        'Conflicto detectado: el empleado ya tiene un turno asignado en ese rango de fechas.';
      this.tipoMensaje = 'error';
      return;
    }

    const nuevoId =
      this.asignaciones.length > 0
        ? Math.max(...this.asignaciones.map((asignacion) => asignacion.id)) + 1
        : 1;

    const nuevaAsignacion: AsignacionTurno = {
      id: nuevoId,
      empleadoId: empleado.id,
      empleado: empleado.nombre,
      departamento: empleado.departamento,
      turnoId: turno.id,
      turno: turno.nombre,
      horario: `${turno.horaInicio} - ${turno.horaFin}`,
      fechaI: this.fechaI,
      fechaF: this.fechaF,
      estado: 'Asignado',
    };

    this.asignaciones = [nuevaAsignacion, ...this.asignaciones];

    this.empleadoSeleccionadoId = null;
    this.turnoSeleccionadoId = null;
    this.fechaI = '';
    this.fechaF = '';

    this.mensaje = 'Turno asignado correctamente.';
    this.tipoMensaje = 'ok';
  }

  anularAsignacion(id: number): void {
    this.asignaciones = this.asignaciones.filter(
      (asignacion) => asignacion.id !== id
    );

    this.mensaje = 'Asignación anulada correctamente.';
    this.tipoMensaje = 'ok';
  }
}