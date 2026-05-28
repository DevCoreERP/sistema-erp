import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { forkJoin } from 'rxjs';

import { Sidebar } from '../../../components/sidebar/sidebar';
import { Topbar } from '../../../components/topbar/topbar';

import { TurnosService, Turno as TurnoBackend } from '../../../../../core/services/turnos.service';
import {
  AsignacionTurnosService,
  AsignacionTurno as AsignacionTurnoBackend,
  AsignacionTurnoRequest,
} from '../../../../../core/services/asignacion-turnos.service';
import { UsuariosService, Usuario as UsuarioBackend } from '../../../../../core/services/usuarios.service';

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
export class AsignacionTurnos implements OnInit {
  private turnosService = inject(TurnosService);
  private asignacionTurnosService = inject(AsignacionTurnosService);
  private usuariosService = inject(UsuariosService);

  busquedaEmpleado = '';

  empleadoSeleccionadoId: number | null = null;
  turnoSeleccionadoId: number | null = null;

  fechaI = '';
  fechaF = '';

  mensaje = '';
  tipoMensaje: 'ok' | 'error' | '' = '';

  empleados: Empleado[] = [];
  turnos: Turno[] = [];
  asignaciones: AsignacionTurno[] = [];

  ngOnInit(): void {
    this.cargarDatos();
  }

  cargarDatos(): void {
    forkJoin({
      usuarios: this.usuariosService.listar(),
      turnos: this.turnosService.listar(),
      asignaciones: this.asignacionTurnosService.listar(),
    }).subscribe({
      next: ({ usuarios, turnos, asignaciones }) => {
        this.empleados = usuarios.map((usuario) => this.mapearUsuario(usuario));
        this.turnos = turnos.map((turno) => this.mapearTurno(turno));
        this.asignaciones = asignaciones.map((asignacion) =>
          this.mapearAsignacion(asignacion)
        );
      },
      error: (err) => {
        console.error('Error al cargar asignaciones:', err);

        if (err.status === 401 || err.status === 403) {
          this.mensaje = 'No autorizado. Debe iniciar sesión con un usuario ADMIN válido.';
          this.tipoMensaje = 'error';
          return;
        }

        this.mensaje = 'No se pudieron cargar los datos desde el backend.';
        this.tipoMensaje = 'error';
      },
    });
  }

  mapearUsuario(usuario: UsuarioBackend): Empleado {
    const nombreCompleto = `${usuario.firstName ?? ''} ${usuario.surnames ?? ''}`.trim();

    return {
      id: usuario.id,
      nombre: nombreCompleto || usuario.username || 'Usuario sin nombre',
      correo: usuario.email ?? 'Sin correo',
      cargo: usuario.roleNames?.join(', ') || 'Sin cargo',
      departamento: 'Sin departamento',
      estado: usuario.estado === false ? 'Inactivo' : 'Activo',
    };
  }

  mapearTurno(turno: TurnoBackend): Turno {
    return {
      id: turno.id,
      nombre: turno.nombre,
      horaInicio: turno.horaInicio,
      horaFin: turno.horaFin,
      descripcion: turno.descripcion,
      estado: turno.estado,
    };
  }

  mapearAsignacion(asignacion: AsignacionTurnoBackend): AsignacionTurno {
    const empleadoId = asignacion.usuarioId ?? asignacion.empleadoId ?? 0;
    const empleado = this.empleados.find((item) => item.id === empleadoId);
    const turno = this.turnos.find((item) => item.id === asignacion.turnoId);

    const fechaInicio = asignacion.fechaInicio ?? asignacion.fechaI ?? '';
    const fechaFin = asignacion.fechaFin ?? asignacion.fechaF ?? '';

    return {
      id: asignacion.id,
      empleadoId,
      empleado: empleado?.nombre ?? `Usuario ${empleadoId}`,
      departamento: empleado?.departamento ?? 'Sin departamento',
      turnoId: asignacion.turnoId,
      turno: turno?.nombre ?? `Turno ${asignacion.turnoId}`,
      horario: turno ? `${turno.horaInicio} - ${turno.horaFin}` : 'Sin horario',
      fechaI: fechaInicio,
      fechaF: fechaFin,
      estado: this.normalizarEstadoAsignacion(asignacion.estado),
    };
  }

  normalizarEstadoAsignacion(estado: string): 'Asignado' | 'Pendiente' | 'Conflicto' {
    if (estado === 'Pendiente') return 'Pendiente';
    if (estado === 'Conflicto') return 'Conflicto';
    return 'Asignado';
  }

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

    const request: AsignacionTurnoRequest = {
      usuarioId: empleado.id,
      turnoId: turno.id,
      fechaInicio: this.fechaI,
      fechaFin: this.fechaF,
    };

    this.asignacionTurnosService.crear(request).subscribe({
      next: () => {
        this.empleadoSeleccionadoId = null;
        this.turnoSeleccionadoId = null;
        this.fechaI = '';
        this.fechaF = '';

        this.mensaje = 'Turno asignado correctamente.';
        this.tipoMensaje = 'ok';

        this.cargarDatos();
      },
      error: (err) => {
        console.error('Error al asignar turno:', err);

        if (err.status === 401 || err.status === 403) {
          this.mensaje = 'No autorizado. Debe iniciar sesión con un usuario ADMIN válido.';
          this.tipoMensaje = 'error';
          return;
        }

        this.mensaje = 'No se pudo asignar el turno desde el backend.';
        this.tipoMensaje = 'error';
      },
    });
  }

  anularAsignacion(id: number): void {
    this.asignacionTurnosService.cambiarEstado(id, 'Inactivo').subscribe({
      next: () => {
        this.mensaje = 'Asignación anulada correctamente.';
        this.tipoMensaje = 'ok';

        this.cargarDatos();
      },
      error: (err) => {
        console.error('Error al anular asignación:', err);

        this.mensaje = 'No se pudo anular la asignación.';
        this.tipoMensaje = 'error';
      },
    });
  }
}