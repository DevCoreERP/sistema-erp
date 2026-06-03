import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { Sidebar } from '../../../components/sidebar/sidebar';
import { Topbar } from '../../../components/topbar/topbar';

import {
  SaasFeatureKey,
  SaasPlanService,
} from '../../../../../core/services/saas-plan.service';

type EstadoNombre = 'Pendiente' | 'Aprobado' | 'Rechazado' | 'Anulado';

interface Usuario {
  id: number;
  nombre: string;
  apellido: string;
  correo: string;
}

interface TipoSolicitudPermiso {
  id: number;
  nombre: string;
  descripcion: string;
  requiereDocumento: boolean;
  remunerado: boolean;
  estado: 'Activo' | 'Inactivo';
}

interface EstadoSolicitud {
  id: number;
  nombre: EstadoNombre;
}

interface SolicitudPermiso {
  id: number;
  usuarioId: number;
  tipoSolicitudPermisoId: number;
  estadoSolicitudId: number;
  fechaI: string;
  fechaF: string;
  motivo: string;
  fechaSolicitud: string;
  observacionRevision: string;
}

@Component({
  selector: 'app-permisos-home',
  standalone: true,
  imports: [CommonModule, FormsModule, Sidebar, Topbar],
  templateUrl: './permisos-home.html',
  styleUrl: './permisos-home.css',
})
export class PermisosHome {
  private saasPlanService = inject(SaasPlanService);

  // ── Datos simulados ──────────────────────────────────────────
  usuarios: Usuario[] = [
    {
      id: 1,
      nombre: 'Laura',
      apellido: 'Mendoza',
      correo: 'laura.mendoza@empresa.com',
    },
    {
      id: 2,
      nombre: 'Carlos',
      apellido: 'Quispe',
      correo: 'carlos.quispe@empresa.com',
    },
    {
      id: 3,
      nombre: 'María',
      apellido: 'Torres',
      correo: 'maria.torres@empresa.com',
    },
    {
      id: 4,
      nombre: 'Diego',
      apellido: 'Vargas',
      correo: 'diego.vargas@empresa.com',
    },
    {
      id: 5,
      nombre: 'Sofía',
      apellido: 'Rojas',
      correo: 'sofia.rojas@empresa.com',
    },
  ];

  tiposSolicitudPermiso: TipoSolicitudPermiso[] = [
    {
      id: 1,
      nombre: 'Permiso personal',
      descripcion: 'Ausencia por asuntos personales no especificados.',
      requiereDocumento: false,
      remunerado: true,
      estado: 'Activo',
    },
    {
      id: 2,
      nombre: 'Permiso médico',
      descripcion: 'Ausencia por cita o tratamiento médico.',
      requiereDocumento: true,
      remunerado: true,
      estado: 'Activo',
    },
    {
      id: 3,
      nombre: 'Permiso familiar',
      descripcion: 'Ausencia por situaciones de carácter familiar.',
      requiereDocumento: false,
      remunerado: true,
      estado: 'Activo',
    },
    {
      id: 4,
      nombre: 'Permiso por estudios',
      descripcion: 'Ausencia por actividades académicas o de formación.',
      requiereDocumento: true,
      remunerado: false,
      estado: 'Activo',
    },
    {
      id: 5,
      nombre: 'Permiso administrativo',
      descripcion: 'Ausencia para trámites administrativos o legales.',
      requiereDocumento: true,
      remunerado: false,
      estado: 'Inactivo',
    },
  ];

  estadosSolicitud: EstadoSolicitud[] = [
    { id: 1, nombre: 'Pendiente' },
    { id: 2, nombre: 'Aprobado' },
    { id: 3, nombre: 'Rechazado' },
    { id: 4, nombre: 'Anulado' },
  ];

  solicitudesPermiso: SolicitudPermiso[] = [];

  private nextId = 1;

  // ── Formulario ────────────────────────────────────────────────
  formUsuarioId: number | '' = '';
  formTipoId: number | '' = '';
  formFechaI = '';
  formFechaF = '';
  formMotivo = '';

  modoFormulario: 'crear' | 'editar' = 'crear';
  solicitudEditandoId: number | null = null;

  // ── Filtros ──────────────────────────────────────────────────
  filtroBusqueda = '';
  filtroTipo: number | '' = '';
  filtroEstado: number | '' = '';

  // ── Feedback ─────────────────────────────────────────────────
  mensaje = '';
  tipoMensaje: 'ok' | 'error' | '' = '';
  private mensajeTimeout: ReturnType<typeof setTimeout> | null = null;

  // ── Modal detalle ────────────────────────────────────────────
  modalVisible = false;
  solicitudDetalle: SolicitudPermiso | null = null;
  observacionModal = '';

  // ── Modal de revisión para rechazar/anular ───────────────────
  modalRevisionVisible = false;
  solicitudRevision: SolicitudPermiso | null = null;
  accionRevision: 'Rechazado' | 'Anulado' | null = null;
  motivoRevision = '';

  // ── Modal bloqueo SaaS ───────────────────────────────────────
  bloqueoVisible = false;
  bloqueoTitulo = '';
  bloqueoMensaje = '';
  bloqueoPlanActual = '';
  bloqueoPlanRequerido = '';

  // ─────────────────────────────────────────────────────────────
  // PLAN SAAS
  // ─────────────────────────────────────────────────────────────
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

  bloquearSiNoTienePermisos(titulo: string): boolean {
    if (this.accionBloqueada('permisos')) {
      this.mostrarBloqueo('permisos', titulo);
      return true;
    }

    return false;
  }

  // ─────────────────────────────────────────────────────────────
  // GETTERS
  // ─────────────────────────────────────────────────────────────
  get totalSolicitudes(): number {
    return this.solicitudesPermiso.length;
  }

  get totalPendientes(): number {
    return this.solicitudesPermiso.filter(
      (s) => this.obtenerEstadoSolicitud(s.estadoSolicitudId)?.nombre === 'Pendiente'
    ).length;
  }

  get totalAprobadas(): number {
    return this.solicitudesPermiso.filter(
      (s) => this.obtenerEstadoSolicitud(s.estadoSolicitudId)?.nombre === 'Aprobado'
    ).length;
  }

  get totalRechazadas(): number {
    return this.solicitudesPermiso.filter(
      (s) => this.obtenerEstadoSolicitud(s.estadoSolicitudId)?.nombre === 'Rechazado'
    ).length;
  }

  get totalAnuladas(): number {
    return this.solicitudesPermiso.filter(
      (s) => this.obtenerEstadoSolicitud(s.estadoSolicitudId)?.nombre === 'Anulado'
    ).length;
  }

  get solicitudesFiltradas(): SolicitudPermiso[] {
    const termino = this.filtroBusqueda.toLowerCase().trim();

    return this.solicitudesPermiso.filter((s) => {
      const usuario = this.obtenerUsuario(s.usuarioId);
      const tipo = this.obtenerTipoSolicitud(s.tipoSolicitudPermisoId);

      const nombreCompleto = usuario
        ? `${usuario.nombre} ${usuario.apellido}`.toLowerCase()
        : '';

      const tipoNombre = tipo?.nombre.toLowerCase() ?? '';
      const motivo = s.motivo.toLowerCase();

      const coincideTexto =
        !termino ||
        nombreCompleto.includes(termino) ||
        tipoNombre.includes(termino) ||
        motivo.includes(termino);

      const coincideTipo =
        !this.filtroTipo || s.tipoSolicitudPermisoId === Number(this.filtroTipo);

      const coincideEstado =
        !this.filtroEstado || s.estadoSolicitudId === Number(this.filtroEstado);

      return coincideTexto && coincideTipo && coincideEstado;
    });
  }

  // ─────────────────────────────────────────────────────────────
  // HELPERS
  // ─────────────────────────────────────────────────────────────
  obtenerUsuario(id: number): Usuario | undefined {
    return this.usuarios.find((u) => u.id === id);
  }

  obtenerTipoSolicitud(id: number): TipoSolicitudPermiso | undefined {
    return this.tiposSolicitudPermiso.find((t) => t.id === id);
  }

  obtenerEstadoSolicitud(id: number): EstadoSolicitud | undefined {
    return this.estadosSolicitud.find((e) => e.id === id);
  }

  obtenerIdEstadoPorNombre(nombre: EstadoNombre): number {
    return this.estadosSolicitud.find((e) => e.nombre === nombre)?.id ?? 1;
  }

  puedeGestionarse(solicitud: SolicitudPermiso): boolean {
    const estado = this.obtenerEstadoSolicitud(solicitud.estadoSolicitudId);
    return estado?.nombre === 'Pendiente';
  }

  claseEstado(estadoId: number): string {
    const nombre = this.obtenerEstadoSolicitud(estadoId)?.nombre;

    if (nombre === 'Pendiente') return 'badge-pendiente';
    if (nombre === 'Aprobado') return 'badge-aprobado';
    if (nombre === 'Rechazado') return 'badge-rechazado';
    if (nombre === 'Anulado') return 'badge-anulado';

    return '';
  }

  textoCorto(texto: string, max = 50): string {
    if (!texto) return '—';
    return texto.length > max ? texto.substring(0, max) + '...' : texto;
  }

  fechaHoy(): string {
    return new Date().toISOString().split('T')[0];
  }

  // ─────────────────────────────────────────────────────────────
  // CRUD
  // ─────────────────────────────────────────────────────────────
  guardarSolicitud(): void {
    if (this.bloquearSiNoTienePermisos('Registrar solicitud de permiso')) {
      return;
    }

    if (!this.formUsuarioId) {
      this.mostrarMensaje('El campo Usuario es obligatorio.', 'error');
      return;
    }

    if (!this.formTipoId) {
      this.mostrarMensaje('El campo Tipo de permiso es obligatorio.', 'error');
      return;
    }

    if (!this.formFechaI) {
      this.mostrarMensaje('La Fecha inicio es obligatoria.', 'error');
      return;
    }

    if (!this.formFechaF) {
      this.mostrarMensaje('La Fecha fin es obligatoria.', 'error');
      return;
    }

    if (!this.formMotivo.trim()) {
      this.mostrarMensaje('El campo Motivo es obligatorio.', 'error');
      return;
    }

    if (this.formFechaI > this.formFechaF) {
      this.mostrarMensaje(
        'La fecha inicio no puede ser mayor que la fecha fin.',
        'error'
      );
      return;
    }

    if (this.modoFormulario === 'crear') {
      this.crearSolicitud();
    } else {
      this.actualizarSolicitud();
    }
  }

  crearSolicitud(): void {
    const nueva: SolicitudPermiso = {
      id: this.nextId++,
      usuarioId: Number(this.formUsuarioId),
      tipoSolicitudPermisoId: Number(this.formTipoId),
      estadoSolicitudId: this.obtenerIdEstadoPorNombre('Pendiente'),
      fechaI: this.formFechaI,
      fechaF: this.formFechaF,
      motivo: this.formMotivo.trim(),
      fechaSolicitud: this.fechaHoy(),
      observacionRevision: '',
    };

    this.solicitudesPermiso = [...this.solicitudesPermiso, nueva];
    this.mostrarMensaje('Solicitud registrada correctamente.', 'ok');
    this.limpiarFormulario();
  }

  actualizarSolicitud(): void {
    if (this.bloquearSiNoTienePermisos('Editar solicitud de permiso')) {
      return;
    }

    this.solicitudesPermiso = this.solicitudesPermiso.map((s) =>
      s.id === this.solicitudEditandoId
        ? {
            ...s,
            usuarioId: Number(this.formUsuarioId),
            tipoSolicitudPermisoId: Number(this.formTipoId),
            fechaI: this.formFechaI,
            fechaF: this.formFechaF,
            motivo: this.formMotivo.trim(),
          }
        : s
    );

    this.mostrarMensaje('Solicitud actualizada correctamente.', 'ok');
    this.limpiarFormulario();
  }

  editarSolicitud(solicitud: SolicitudPermiso): void {
    if (this.bloquearSiNoTienePermisos('Editar solicitud de permiso')) {
      return;
    }

    if (!this.puedeGestionarse(solicitud)) {
      this.mostrarMensaje(
        'Solo se pueden editar solicitudes en estado Pendiente.',
        'error'
      );
      return;
    }

    this.modoFormulario = 'editar';
    this.solicitudEditandoId = solicitud.id;
    this.formUsuarioId = solicitud.usuarioId;
    this.formTipoId = solicitud.tipoSolicitudPermisoId;
    this.formFechaI = solicitud.fechaI;
    this.formFechaF = solicitud.fechaF;
    this.formMotivo = solicitud.motivo;

    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  cambiarEstado(
    id: number,
    nuevoEstadoNombre: EstadoNombre,
    observacion = ''
  ): void {
    const nuevoEstadoId = this.obtenerIdEstadoPorNombre(nuevoEstadoNombre);

    this.solicitudesPermiso = this.solicitudesPermiso.map((s) =>
      s.id === id
        ? {
            ...s,
            estadoSolicitudId: nuevoEstadoId,
            observacionRevision: observacion,
          }
        : s
    );
  }

  aprobarSolicitud(id: number): void {
    if (this.bloquearSiNoTienePermisos('Aprobar solicitud de permiso')) {
      return;
    }

    const solicitud = this.solicitudesPermiso.find((x) => x.id === id);

    if (!solicitud || !this.puedeGestionarse(solicitud)) {
      this.mostrarMensaje(
        'Solo se pueden aprobar solicitudes en estado Pendiente.',
        'error'
      );
      return;
    }

    this.cambiarEstado(id, 'Aprobado');
    this.mostrarMensaje('Solicitud aprobada correctamente.', 'ok');

    if (this.solicitudDetalle?.id === id) {
      this.solicitudDetalle =
        this.solicitudesPermiso.find((x) => x.id === id) ?? null;
    }
  }

  rechazarSolicitud(id: number): void {
    if (this.bloquearSiNoTienePermisos('Rechazar solicitud de permiso')) {
      return;
    }

    this.abrirModalRevision(id, 'Rechazado');
  }

  anularSolicitud(id: number): void {
    if (this.bloquearSiNoTienePermisos('Anular solicitud de permiso')) {
      return;
    }

    this.abrirModalRevision(id, 'Anulado');
  }

  abrirModalRevision(id: number, accion: 'Rechazado' | 'Anulado'): void {
    if (this.bloquearSiNoTienePermisos(`${accion} solicitud de permiso`)) {
      return;
    }

    const solicitud = this.solicitudesPermiso.find((x) => x.id === id);

    if (!solicitud || !this.puedeGestionarse(solicitud)) {
      this.mostrarMensaje(
        'Solo se pueden gestionar solicitudes en estado Pendiente.',
        'error'
      );
      return;
    }

    this.solicitudRevision = { ...solicitud };
    this.accionRevision = accion;
    this.motivoRevision = '';
    this.modalRevisionVisible = true;
  }

  cerrarModalRevision(): void {
    this.modalRevisionVisible = false;
    this.solicitudRevision = null;
    this.accionRevision = null;
    this.motivoRevision = '';
  }

  cerrarModalRevisionPorOverlay(event: MouseEvent): void {
    if (
      (event.target as HTMLElement).classList.contains('modal-revision-overlay')
    ) {
      this.cerrarModalRevision();
    }
  }

  confirmarRevision(): void {
    if (this.bloquearSiNoTienePermisos('Confirmar revisión de permiso')) {
      return;
    }

    if (!this.solicitudRevision || !this.accionRevision) {
      return;
    }

    const motivoLimpio = this.motivoRevision.trim();

    const observacion =
      motivoLimpio ||
      (this.accionRevision === 'Rechazado'
        ? 'Solicitud rechazada desde el módulo de permisos.'
        : 'Solicitud anulada desde el módulo de permisos.');

    this.cambiarEstado(
      this.solicitudRevision.id,
      this.accionRevision,
      observacion
    );

    this.mostrarMensaje(
      this.accionRevision === 'Rechazado'
        ? 'Solicitud rechazada correctamente.'
        : 'Solicitud anulada correctamente.',
      'ok'
    );

    if (this.solicitudDetalle?.id === this.solicitudRevision.id) {
      this.solicitudDetalle =
        this.solicitudesPermiso.find(
          (x) => x.id === this.solicitudRevision!.id
        ) ?? null;
    }

    this.cerrarModalRevision();
  }

  get tituloModalRevision(): string {
    return this.accionRevision === 'Rechazado'
      ? 'Rechazar solicitud'
      : 'Anular solicitud';
  }

  get textoBotonRevision(): string {
    return this.accionRevision === 'Rechazado'
      ? 'Confirmar rechazo'
      : 'Confirmar anulación';
  }

  // ─────────────────────────────────────────────────────────────
  // MODAL DETALLE
  // ─────────────────────────────────────────────────────────────
  verDetalle(solicitud: SolicitudPermiso): void {
    this.solicitudDetalle = { ...solicitud };
    this.observacionModal = '';
    this.modalVisible = true;
  }

  cerrarModal(): void {
    this.modalVisible = false;
    this.solicitudDetalle = null;
    this.observacionModal = '';
  }

  cerrarModalPorOverlay(event: MouseEvent): void {
    if ((event.target as HTMLElement).classList.contains('modal-overlay')) {
      this.cerrarModal();
    }
  }

  aprobarDesdeModal(): void {
    if (this.solicitudDetalle) {
      this.aprobarSolicitud(this.solicitudDetalle.id);
      this.solicitudDetalle =
        this.solicitudesPermiso.find(
          (x) => x.id === this.solicitudDetalle!.id
        ) ?? null;
    }
  }

  rechazarDesdeModal(): void {
    if (this.solicitudDetalle) {
      const id = this.solicitudDetalle.id;
      this.cerrarModal();
      this.rechazarSolicitud(id);
    }
  }

  // ─────────────────────────────────────────────────────────────
  // UTILIDADES
  // ─────────────────────────────────────────────────────────────
  limpiarFormulario(): void {
    this.formUsuarioId = '';
    this.formTipoId = '';
    this.formFechaI = '';
    this.formFechaF = '';
    this.formMotivo = '';
    this.modoFormulario = 'crear';
    this.solicitudEditandoId = null;
  }

  cancelarEdicion(): void {
    this.limpiarFormulario();
    this.mostrarMensaje('Edición cancelada.', 'ok');
  }

  limpiarFiltros(): void {
    this.filtroBusqueda = '';
    this.filtroTipo = '';
    this.filtroEstado = '';
  }

  mostrarMensaje(texto: string, tipo: 'ok' | 'error'): void {
    this.mensaje = texto;
    this.tipoMensaje = tipo;

    if (this.mensajeTimeout) {
      clearTimeout(this.mensajeTimeout);
    }

    this.mensajeTimeout = setTimeout(() => {
      this.mensaje = '';
      this.tipoMensaje = '';
    }, 4500);
  }
}