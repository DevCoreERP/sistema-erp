import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

import { Sidebar } from '../../../components/sidebar/sidebar';
import { Topbar } from '../../../components/topbar/topbar';

import {
  TurnosService,
  Turno as TurnoBackend,
} from '../../../../../core/services/turnos.service';

import {
  AsignacionTurnosService,
  AsignacionTurno as AsignacionTurnoBackend,
} from '../../../../../core/services/asignacion-turnos.service';

import {
  UsuariosService,
  Usuario as UsuarioBackend,
} from '../../../../../core/services/usuarios.service';

import {
  SaasPlanService,
  SaasFeatureKey,
} from '../../../../../core/services/saas-plan.service';

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
  private saasPlanService = inject(SaasPlanService);

  // Campo de búsqueda general en panel derecho
  busquedaEmpleado = '';

  // Doble opción para escoger empleado
  textoEmpleado = '';
  empleadoSeleccionadoId: number | null = null;

  // Turno y fechas
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
      usuarios: this.usuariosService.listar().pipe(catchError(() => of([]))),
      turnos: this.turnosService.listar().pipe(catchError(() => of([]))),
      asignaciones: this.asignacionTurnosService.listar().pipe(catchError(() => of([]))),
    }).subscribe({
      next: ({ usuarios, turnos, asignaciones }) => {
        this.empleados =
          usuarios.length > 0
            ? usuarios.map((usuario) => this.mapearUsuario(usuario))
            : this.obtenerEmpleadosDemo();

        this.turnos =
          turnos.length > 0
            ? turnos.map((turno) => this.mapearTurno(turno))
            : this.obtenerTurnosDemo();

        this.asignaciones =
          asignaciones.length > 0
            ? asignaciones.map((asignacion) => this.mapearAsignacion(asignacion))
            : [];
      },
      error: () => {
        this.empleados = this.obtenerEmpleadosDemo();
        this.turnos = this.obtenerTurnosDemo();
        this.asignaciones = [];
      },
    });
  }

  // =========================
  // MAPEO BACKEND -> FRONT
  // =========================

  private mapearUsuario(usuario: UsuarioBackend): Empleado {
    const nombreCompleto = `${usuario.firstName ?? ''} ${usuario.surnames ?? ''}`.trim();

    return {
      id: usuario.id,
      nombre: nombreCompleto || usuario.username || 'Usuario sin nombre',
      correo: usuario.email ?? 'Sin correo',
      cargo: usuario.roleNames?.join(', ') || 'Sin cargo',
      departamento: 'Recursos Humanos',
      estado: usuario.estado === false ? 'Inactivo' : 'Activo',
    };
  }

  private mapearTurno(turno: TurnoBackend): Turno {
    return {
      id: turno.id,
      nombre: turno.nombre,
      horaInicio: turno.horaInicio,
      horaFin: turno.horaFin,
      descripcion: turno.descripcion || '',
      estado: turno.estado,
    };
  }

  private mapearAsignacion(asignacion: AsignacionTurnoBackend): AsignacionTurno {
    const empleadoId = (asignacion as any).usuarioId ?? (asignacion as any).empleadoId ?? 0;
    const turnoId = (asignacion as any).turnoId ?? 0;

    const empleado = this.empleados.find((item) => item.id === empleadoId);
    const turno = this.turnos.find((item) => item.id === turnoId);

    const fechaInicio = (asignacion as any).fechaInicio ?? (asignacion as any).fechaI ?? '';
    const fechaFin = (asignacion as any).fechaFin ?? (asignacion as any).fechaF ?? '';

    return {
      id: (asignacion as any).id,
      empleadoId,
      empleado: empleado?.nombre ?? `Empleado ${empleadoId}`,
      departamento: empleado?.departamento ?? 'Sin departamento',
      turnoId,
      turno: turno?.nombre ?? `Turno ${turnoId}`,
      horario: turno ? `${turno.horaInicio} - ${turno.horaFin}` : 'Sin horario',
      fechaI: fechaInicio,
      fechaF: fechaFin,
      estado: this.normalizarEstadoAsignacion((asignacion as any).estado),
    };
  }

  private normalizarEstadoAsignacion(
    estado: string
  ): 'Asignado' | 'Pendiente' | 'Conflicto' {
    if (estado === 'Pendiente') return 'Pendiente';
    if (estado === 'Conflicto') return 'Conflicto';
    return 'Asignado';
  }

  // =========================
  // DATOS DEMO
  // =========================

  private obtenerEmpleadosDemo(): Empleado[] {
    return [
      {
        id: 1,
        nombre: 'María Fernanda Rojas',
        correo: 'maria.rojas@empresa.com',
        cargo: 'Analista RRHH',
        departamento: 'Recursos Humanos',
        estado: 'Activo',
      },
      {
        id: 2,
        nombre: 'Carlos Andrés Pérez',
        correo: 'carlos.perez@empresa.com',
        cargo: 'Supervisor',
        departamento: 'Operaciones',
        estado: 'Activo',
      },
      {
        id: 3,
        nombre: 'Lucía Gómez Suárez',
        correo: 'lucia.gomez@empresa.com',
        cargo: 'Asistente Administrativa',
        departamento: 'Administración',
        estado: 'Activo',
      },
    ];
  }

  private obtenerTurnosDemo(): Turno[] {
    return [
      {
        id: 1,
        nombre: 'Mañana',
        horaInicio: '08:00',
        horaFin: '16:00',
        descripcion: 'Turno matutino',
        estado: 'Activo',
      },
      {
        id: 2,
        nombre: 'Tarde',
        horaInicio: '16:00',
        horaFin: '00:00',
        descripcion: 'Turno vespertino',
        estado: 'Activo',
      },
      {
        id: 3,
        nombre: 'Noche',
        horaInicio: '00:00',
        horaFin: '08:00',
        descripcion: 'Turno nocturno',
        estado: 'Activo',
      },
    ];
  }

  // =========================
  // PLAN SAAS
  // =========================

  get featureKey(): SaasFeatureKey {
    return 'turnos' as SaasFeatureKey;
  }

  get planActualTexto(): string {
    return this.saasPlanService.getPlanLabel(this.saasPlanService.getActivePlan());
  }

  puedeAsignar(): boolean {
    return this.saasPlanService.canAccess(this.featureKey);
  }

  // =========================
  // GETTERS
  // =========================

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

  get empleadosParaSeleccion(): Empleado[] {
    const texto = this.textoEmpleado.toLowerCase().trim();

    if (!texto) {
      return this.empleadosActivos;
    }

    return this.empleadosActivos.filter((empleado) =>
      empleado.nombre.toLowerCase().includes(texto)
    );
  }

  get totalAsignaciones(): number {
    return this.asignaciones.length;
  }

  get totalAsignadas(): number {
    return this.asignaciones.filter((item) => item.estado === 'Asignado').length;
  }

  get totalPendientes(): number {
    return this.asignaciones.filter((item) => item.estado === 'Pendiente').length;
  }

  get totalConflictos(): number {
    return this.asignaciones.filter((item) => item.estado === 'Conflicto').length;
  }

  // =========================
  // SINCRONIZACIÓN EMPLEADO
  // =========================

  sincronizarEmpleadoDesdeTexto(): void {
    const texto = this.textoEmpleado.trim().toLowerCase();

    if (!texto) {
      this.empleadoSeleccionadoId = null;
      return;
    }

    const coincidencia = this.empleadosActivos.find((empleado) =>
      empleado.nombre.toLowerCase().includes(texto)
    );

    if (coincidencia) {
      this.empleadoSeleccionadoId = coincidencia.id;
    }
  }

  sincronizarTextoDesdeSelect(): void {
    const empleado = this.empleados.find(
      (item) => item.id === Number(this.empleadoSeleccionadoId)
    );

    if (empleado) {
      this.textoEmpleado = empleado.nombre;
    }
  }

  // =========================
  // ACCIÓN PRINCIPAL
  // =========================

  intentarAsignar(): void {
    this.mensaje = '';
    this.tipoMensaje = '';

    if (!this.puedeAsignar()) {
      this.mensaje = `Tu plan actual (${this.planActualTexto}) no permite registrar asignaciones. Requiere al menos el plan Esencial.`;
      this.tipoMensaje = 'error';
      return;
    }

    this.asignarTurno();
  }

  asignarTurno(): void {
    if (
      !this.empleadoSeleccionadoId ||
      !this.turnoSeleccionadoId ||
      !this.fechaI ||
      !this.fechaF
    ) {
      this.mensaje = 'Debes completar todos los campos para registrar la asignación.';
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
      this.mensaje = 'No se pudo identificar el empleado o el turno seleccionado.';
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
        'Conflicto detectado: el empleado ya tiene una asignación dentro de ese rango de fechas.';
      this.tipoMensaje = 'error';
      return;
    }

    const nuevaAsignacion: AsignacionTurno = {
      id: this.asignaciones.length > 0
        ? Math.max(...this.asignaciones.map((item) => item.id)) + 1
        : 1,
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

    this.mensaje = 'Asignación registrada correctamente.';
    this.tipoMensaje = 'ok';

    this.limpiarFormulario();
  }

  anularAsignacion(id: number): void {
    this.asignaciones = this.asignaciones.filter((item) => item.id !== id);
    this.mensaje = 'La asignación fue anulada correctamente.';
    this.tipoMensaje = 'ok';
  }

  limpiarFormulario(): void {
    this.textoEmpleado = '';
    this.empleadoSeleccionadoId = null;
    this.turnoSeleccionadoId = null;
    this.fechaI = '';
    this.fechaF = '';
  }
}