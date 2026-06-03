import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { Sidebar } from '../../../components/sidebar/sidebar';
import { Topbar } from '../../../components/topbar/topbar';

import {
  SaasFeatureKey,
  SaasPlanService,
} from '../../../../../core/services/saas-plan.service';

type EstadoSolicitud = 'PENDIENTE' | 'APROBADA' | 'RECHAZADA' | 'ANULADA';

interface UsuarioVacacion {
  idUsuario: number;
  nombre: string;
  correo: string;
}

interface SolicitudVacacion {
  idSolicitudVacacion: number;
  usuarioId: number;
  fechaInicio: string;
  fechaFin: string;
  diasSolicitados: number;
  fechaSolicitud: string;
  estado: EstadoSolicitud;
  motivo: string;
  observacionRevision: string;
  gestion: number;
}

@Component({
  selector: 'app-vacaciones-home',
  standalone: true,
  imports: [CommonModule, FormsModule, Sidebar, Topbar],
  templateUrl: './vacaciones-home.html',
  styleUrl: './vacaciones-home.css',
})
export class VacacionesHome {
  private saasPlanService = inject(SaasPlanService);

  estadosSolicitud: EstadoSolicitud[] = [
    'PENDIENTE',
    'APROBADA',
    'RECHAZADA',
    'ANULADA',
  ];

  usuarios: UsuarioVacacion[] = [];

  solicitudesVacacion: SolicitudVacacion[] = [];

  nuevaSolicitud = {
    usuarioNombre: '',
    usuarioCorreo: '',
    fechaInicio: '',
    fechaFin: '',
    fechaSolicitud: this.obtenerFechaActual(),
    estado: 'PENDIENTE' as EstadoSolicitud,
    motivo: '',
  };

  filtros = {
    usuario: '',
    estado: 'TODOS',
    gestion: 'TODAS',
    fecha: '',
  };

  solicitudSeleccionada: SolicitudVacacion | null = null;

  solicitudRevision: SolicitudVacacion | null = null;
  accionRevision: 'RECHAZADA' | 'ANULADA' | null = null;
  observacionRevision = '';

  mensajeFormulario = '';

  // MODAL DE BLOQUEO SAAS
  bloqueoVisible = false;
  bloqueoTitulo = '';
  bloqueoMensaje = '';
  bloqueoPlanActual = '';
  bloqueoPlanRequerido = '';

  // =========================================================
  // PLAN SAAS
  // =========================================================

  puedeUsar(feature: SaasFeatureKey): boolean {
    return this.saasPlanService.canAccess(feature);
  }

  accionBloqueada(feature: SaasFeatureKey): boolean {
    return !this.puedeUsar(feature);
  }

  getPlanActual(): string {
    return this.saasPlanService.getPlanLabel(
      this.saasPlanService.getActivePlan()
    );
  }

  mostrarBloqueo(feature: SaasFeatureKey, titulo: string): void {
    const planActual = this.saasPlanService.getActivePlan();
    const planRequerido = this.saasPlanService.getRequiredPlan(feature);

    this.bloqueoTitulo = titulo;
    this.bloqueoMensaje = this.saasPlanService.getLockMessage(feature);
    this.bloqueoPlanActual = this.saasPlanService.getPlanLabel(planActual);
    this.bloqueoPlanRequerido =
      this.saasPlanService.getPlanLabel(planRequerido);
    this.bloqueoVisible = true;
  }

  cerrarBloqueo(): void {
    this.bloqueoVisible = false;
    this.bloqueoTitulo = '';
    this.bloqueoMensaje = '';
    this.bloqueoPlanActual = '';
    this.bloqueoPlanRequerido = '';
  }

  bloquearSiNoTieneVacaciones(titulo: string): boolean {
    if (this.accionBloqueada('vacaciones')) {
      this.mostrarBloqueo('vacaciones', titulo);
      return true;
    }

    return false;
  }

  // =========================================================
  // RESUMEN
  // =========================================================

  get totalSolicitudes(): number {
    return this.solicitudesVacacion.length;
  }

  get totalPendientes(): number {
    return this.solicitudesVacacion.filter(
      (solicitud) => solicitud.estado === 'PENDIENTE'
    ).length;
  }

  get totalAprobadas(): number {
    return this.solicitudesVacacion.filter(
      (solicitud) => solicitud.estado === 'APROBADA'
    ).length;
  }

  get totalRechazadasAnuladas(): number {
    return this.solicitudesVacacion.filter(
      (solicitud) =>
        solicitud.estado === 'RECHAZADA' || solicitud.estado === 'ANULADA'
    ).length;
  }

  get diasSolicitadosCalculados(): number {
    return this.calcularDiasSolicitados(
      this.nuevaSolicitud.fechaInicio,
      this.nuevaSolicitud.fechaFin
    );
  }

  get gestionesDisponibles(): number[] {
    const gestiones = this.solicitudesVacacion.map(
      (solicitud) => solicitud.gestion
    );

    return Array.from(new Set(gestiones)).sort((a, b) => b - a);
  }

  get solicitudesFiltradas(): SolicitudVacacion[] {
    return this.solicitudesVacacion.filter((solicitud) => {
      const nombreUsuario = this.obtenerNombreUsuario(
        solicitud.usuarioId
      ).toLowerCase();

      const cumpleUsuario =
        !this.filtros.usuario.trim() ||
        nombreUsuario.includes(this.filtros.usuario.trim().toLowerCase());

      const cumpleEstado =
        this.filtros.estado === 'TODOS' ||
        solicitud.estado === this.filtros.estado;

      const cumpleGestion =
        this.filtros.gestion === 'TODAS' ||
        solicitud.gestion === Number(this.filtros.gestion);

      const cumpleFecha =
        !this.filtros.fecha ||
        solicitud.fechaInicio === this.filtros.fecha ||
        solicitud.fechaFin === this.filtros.fecha ||
        solicitud.fechaSolicitud === this.filtros.fecha;

      return cumpleUsuario && cumpleEstado && cumpleGestion && cumpleFecha;
    });
  }

  // =========================================================
  // REGISTRAR SOLICITUD
  // =========================================================

  registrarSolicitud(): void {
    this.mensajeFormulario = '';

    if (this.bloquearSiNoTieneVacaciones('Registrar solicitud de vacaciones')) {
      return;
    }

    const nombreUsuario = this.nuevaSolicitud.usuarioNombre.trim();
    const correoUsuario = this.nuevaSolicitud.usuarioCorreo.trim();

    if (!nombreUsuario) {
      this.mensajeFormulario = 'Debe escribir el nombre del usuario.';
      return;
    }

    if (!this.nuevaSolicitud.fechaInicio || !this.nuevaSolicitud.fechaFin) {
      this.mensajeFormulario =
        'Debe seleccionar la fecha de inicio y la fecha fin.';
      return;
    }

    const diasSolicitados = this.diasSolicitadosCalculados;

    if (diasSolicitados <= 0) {
      this.mensajeFormulario =
        'La fecha fin debe ser igual o posterior a la fecha de inicio.';
      return;
    }

    const usuarioId = this.obtenerOCrearUsuario(nombreUsuario, correoUsuario);

    const gestion = this.obtenerGestionDesdeFecha(
      this.nuevaSolicitud.fechaInicio
    );

    const nuevaSolicitudVacacion: SolicitudVacacion = {
      idSolicitudVacacion: this.generarIdSolicitud(),
      usuarioId,
      fechaInicio: this.nuevaSolicitud.fechaInicio,
      fechaFin: this.nuevaSolicitud.fechaFin,
      diasSolicitados,
      fechaSolicitud: this.nuevaSolicitud.fechaSolicitud,
      estado: 'PENDIENTE',
      motivo: this.nuevaSolicitud.motivo.trim() || 'Sin motivo registrado.',
      observacionRevision: '',
      gestion,
    };

    this.solicitudesVacacion = [
      nuevaSolicitudVacacion,
      ...this.solicitudesVacacion,
    ];

    this.nuevaSolicitud = {
      usuarioNombre: '',
      usuarioCorreo: '',
      fechaInicio: '',
      fechaFin: '',
      fechaSolicitud: this.obtenerFechaActual(),
      estado: 'PENDIENTE',
      motivo: '',
    };

    this.mensajeFormulario =
      'Solicitud registrada correctamente en estado Pendiente.';
  }

  // =========================================================
  // DETALLE
  // =========================================================

  verDetalle(solicitud: SolicitudVacacion): void {
    this.solicitudSeleccionada = solicitud;
  }

  cerrarDetalle(): void {
    this.solicitudSeleccionada = null;
  }

  // =========================================================
  // APROBAR / RECHAZAR / ANULAR
  // =========================================================

  aprobarSolicitud(solicitud: SolicitudVacacion): void {
    if (this.bloquearSiNoTieneVacaciones('Aprobar solicitud de vacaciones')) {
      return;
    }

    if (solicitud.estado !== 'PENDIENTE') {
      return;
    }

    solicitud.estado = 'APROBADA';
    solicitud.observacionRevision =
      'Solicitud aprobada desde el módulo de vacaciones.';

    this.cerrarDetalle();
  }

  abrirRevision(
    solicitud: SolicitudVacacion,
    accion: 'RECHAZADA' | 'ANULADA'
  ): void {
    const titulo =
      accion === 'RECHAZADA'
        ? 'Rechazar solicitud de vacaciones'
        : 'Anular solicitud de vacaciones';

    if (this.bloquearSiNoTieneVacaciones(titulo)) {
      return;
    }

    if (!this.puedeRechazarOAnular(solicitud)) {
      return;
    }

    this.solicitudRevision = solicitud;
    this.accionRevision = accion;
    this.observacionRevision = '';
    this.solicitudSeleccionada = null;
  }

  confirmarRevision(): void {
    if (
      this.bloquearSiNoTieneVacaciones(
        'Confirmar revisión de solicitud de vacaciones'
      )
    ) {
      return;
    }

    if (!this.solicitudRevision || !this.accionRevision) {
      return;
    }

    const solicitud = this.solicitudRevision;

    solicitud.estado = this.accionRevision;
    solicitud.observacionRevision =
      this.observacionRevision.trim() ||
      `Solicitud marcada como ${this.accionRevision.toLowerCase()}.`;

    this.cerrarRevision();
  }

  cerrarRevision(): void {
    this.solicitudRevision = null;
    this.accionRevision = null;
    this.observacionRevision = '';
  }

  // =========================================================
  // FILTROS
  // =========================================================

  limpiarFiltros(): void {
    this.filtros = {
      usuario: '',
      estado: 'TODOS',
      gestion: 'TODAS',
      fecha: '',
    };
  }

  // =========================================================
  // USUARIOS
  // =========================================================

  obtenerUsuario(usuarioId: number): UsuarioVacacion | undefined {
    return this.usuarios.find((usuario) => usuario.idUsuario === usuarioId);
  }

  obtenerNombreUsuario(usuarioId: number): string {
    return this.obtenerUsuario(usuarioId)?.nombre || 'Usuario no registrado';
  }

  obtenerCorreoUsuario(usuarioId: number): string {
    return this.obtenerUsuario(usuarioId)?.correo || 'Sin correo registrado';
  }

  // =========================================================
  // UTILIDADES
  // =========================================================

  calcularDiasSolicitados(fechaInicio: string, fechaFin: string): number {
    if (!fechaInicio || !fechaFin) {
      return 0;
    }

    const inicio = new Date(`${fechaInicio}T00:00:00`);
    const fin = new Date(`${fechaFin}T00:00:00`);

    if (Number.isNaN(inicio.getTime()) || Number.isNaN(fin.getTime())) {
      return 0;
    }

    const diferenciaMilisegundos = fin.getTime() - inicio.getTime();

    const dias =
      Math.floor(diferenciaMilisegundos / (1000 * 60 * 60 * 24)) + 1;

    return dias > 0 ? dias : 0;
  }

  obtenerClaseEstado(estado: EstadoSolicitud): string {
    return `estado estado-${estado.toLowerCase()}`;
  }

  puedeAprobar(solicitud: SolicitudVacacion): boolean {
    return solicitud.estado === 'PENDIENTE';
  }

  puedeRechazarOAnular(solicitud: SolicitudVacacion): boolean {
    return solicitud.estado === 'PENDIENTE' || solicitud.estado === 'APROBADA';
  }

  private obtenerOCrearUsuario(nombre: string, correo: string): number {
    const usuarioExistente = this.usuarios.find(
      (usuario) => usuario.nombre.toLowerCase() === nombre.toLowerCase()
    );

    if (usuarioExistente) {
      if (correo) {
        usuarioExistente.correo = correo;
      }

      return usuarioExistente.idUsuario;
    }

    const nuevoUsuario: UsuarioVacacion = {
      idUsuario: this.generarIdUsuario(),
      nombre,
      correo: correo || 'Sin correo registrado',
    };

    this.usuarios = [...this.usuarios, nuevoUsuario];

    return nuevoUsuario.idUsuario;
  }

  private obtenerFechaActual(): string {
    const fecha = new Date();

    const fechaLocal = new Date(
      fecha.getTime() - fecha.getTimezoneOffset() * 60000
    );

    return fechaLocal.toISOString().split('T')[0];
  }

  private obtenerGestionDesdeFecha(fecha: string): number {
    return new Date(`${fecha}T00:00:00`).getFullYear();
  }

  private generarIdSolicitud(): number {
    const ultimoId = this.solicitudesVacacion.reduce(
      (mayor, solicitud) =>
        Math.max(mayor, solicitud.idSolicitudVacacion),
      0
    );

    return ultimoId + 1;
  }

  private generarIdUsuario(): number {
    const ultimoId = this.usuarios.reduce(
      (mayor, usuario) => Math.max(mayor, usuario.idUsuario),
      0
    );

    return ultimoId + 1;
  }
}