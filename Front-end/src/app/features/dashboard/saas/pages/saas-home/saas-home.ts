import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

import { Sidebar } from '../../../components/sidebar/sidebar';
import { Topbar } from '../../../components/topbar/topbar';

export interface PlanDetalle {
  id: string;
  nombre: string;
  precio: number;
  moneda: string;
  usuariosMinimos: number;
  ideal: string;
  modulos: string[];
  beneficios: string[];
}

export interface Suscripcion {
  plan: string;
  estado: string;
  fechaInicio: string;
  vencimiento: string;
  usuariosActivos: number;
  precio?: number;
  moneda?: string;
  usuariosMinimos?: number;
}

export interface MetodoPago {
  tipo: string;
  ultimosCuatro: string;
  titular: string;
  marca?: string;
  actualizadoEn?: string;
}

export interface HistorialPago {
  fecha: string;
  monto: number;
  plan: string;
  metodo: string;
  estado: string;
  recibo: string;
}

@Component({
  selector: 'app-saas-home',
  standalone: true,
  imports: [CommonModule, Sidebar, Topbar],
  templateUrl: './saas-home.html',
  styleUrls: ['./saas-home.css'],
})
export class SaasHome implements OnInit {
  suscripcion: Suscripcion | null = null;
  metodoPago: MetodoPago | null = null;
  historialPagos: HistorialPago[] = [];
  planDetalle: PlanDetalle | null = null;

  mostrarModalCancelacion: boolean = false;
  progresoSuscripcion: number = 0;
  tiempoRestante: string = '';

  private catalogoPlanes: PlanDetalle[] = [
    {
      id: 'esencial',
      nombre: 'Esencial',
      precio: 9,
      moneda: 'USD',
      usuariosMinimos: 50,
      ideal: 'Pymes con necesidades básicas de RRHH',
      modulos: [
        'Core HR',
        'Nómina',
        'Ausencias',
        'Beneficios',
        'Reportes básicos',
        'Autoservicio del empleado',
      ],
      beneficios: [
        'Hosting incluido',
        'Actualizaciones automáticas',
        'Soporte básico 24/7',
        'Seguridad avanzada',
        'Reportes básicos',
        'Sin costos ocultos de mantenimiento',
      ],
    },
    {
      id: 'profesional',
      nombre: 'Profesional',
      precio: 18,
      moneda: 'USD',
      usuariosMinimos: 50,
      ideal: 'Empresas en crecimiento con foco en talento',
      modulos: [
        'Core HR',
        'Nómina',
        'Ausencias',
        'Beneficios',
        'Reportes básicos',
        'Autoservicio del empleado',
        'Reclutamiento',
        'Onboarding',
        'Gestión del desempeño',
        'Capacitación',
        'Desarrollo del talento',
      ],
      beneficios: [
        'Hosting incluido',
        'Actualizaciones automáticas',
        'Soporte básico 24/7',
        'Seguridad avanzada',
        'Reportes ilimitados',
        'Mayor cobertura del ciclo de talento',
      ],
    },
    {
      id: 'premium',
      nombre: 'Premium',
      precio: 25,
      moneda: 'USD',
      usuariosMinimos: 50,
      ideal: 'Grandes empresas con procesos estratégicos de HCM',
      modulos: [
        'Suite completa',
        'Analítica con IA',
        'Sucesión',
        'Compensación variable',
        'Integraciones avanzadas',
        'Soporte prioritario',
        'API ilimitada',
      ],
      beneficios: [
        'Hosting incluido',
        'Actualizaciones automáticas',
        'Seguridad avanzada',
        'Reportes ilimitados',
        'Soporte prioritario',
        'Integraciones avanzadas',
        'API ilimitada',
        'Analítica estratégica con IA',
      ],
    },
  ];

  constructor(private router: Router) {}

  ngOnInit(): void {
    this.cargarDatos();
  }

  cargarDatos(): void {
    this.suscripcion = this.cargarSuscripcion();
    this.metodoPago = this.cargarMetodoPago();
    this.historialPagos = this.cargarHistorialPagos();

    this.planDetalle = this.suscripcion
      ? this.obtenerDetallePlan(this.suscripcion.plan)
      : null;

    this.tiempoRestante = this.calcularTiempoRestante();
    this.progresoSuscripcion = this.calcularProgresoSuscripcion();
  }

  cargarSuscripcion(): Suscripcion | null {
    const valor = this.parseLocalStorageSeguro('saas_active_subscription');

    if (valor && typeof valor === 'object' && valor.plan) {
      return valor as Suscripcion;
    }

    return null;
  }

  cargarMetodoPago(): MetodoPago | null {
    const valor = this.parseLocalStorageSeguro('saas_payment_method');

    if (valor && typeof valor === 'object' && valor.tipo) {
      return valor as MetodoPago;
    }

    return null;
  }

  cargarHistorialPagos(): HistorialPago[] {
    const valor = this.parseLocalStorageSeguro('saas_payment_history');

    if (Array.isArray(valor)) {
      return valor as HistorialPago[];
    }

    return [];
  }

  obtenerDetallePlan(nombrePlan: string): PlanDetalle | null {
    const planNormalizado = this.normalizarTexto(nombrePlan);

    return (
      this.catalogoPlanes.find((plan) => {
        const nombreNormalizado = this.normalizarTexto(plan.nombre);
        const idNormalizado = this.normalizarTexto(plan.id);

        return (
          nombreNormalizado === planNormalizado ||
          idNormalizado === planNormalizado
        );
      }) || null
    );
  }

  irAPlanes(): void {
    try {
      localStorage.removeItem('saas_selected_plan');
      localStorage.setItem('saas_billing_mode', 'new-subscription');
    } catch {
      // Evita romper la navegación si localStorage falla.
    }

    this.router.navigate(['/saas/planes']);
  }

  cambiarPlan(): void {
    try {
      localStorage.removeItem('saas_selected_plan');
      localStorage.setItem('saas_billing_mode', 'change-plan');
    } catch {
      // Evita romper la navegación si localStorage falla.
    }

    this.router.navigate(['/saas/planes']);
  }

  cambiarMetodoPago(): void {
    try {
      localStorage.removeItem('saas_selected_plan');
      localStorage.setItem('saas_billing_mode', 'update-payment-method');
    } catch {
      // Evita romper la navegación si localStorage falla.
    }

    this.router.navigate(['/saas/facturacion']);
  }

  abrirModalCancelacion(): void {
    this.mostrarModalCancelacion = true;
  }

  cerrarModalCancelacion(): void {
    this.mostrarModalCancelacion = false;
  }

  confirmarCancelacion(): void {
    if (!this.suscripcion) {
      this.cerrarModalCancelacion();
      return;
    }

    const suscripcionActualizada: Suscripcion = {
      ...this.suscripcion,
      estado: 'Cancelación programada',
    };

    try {
      localStorage.setItem(
        'saas_active_subscription',
        JSON.stringify(suscripcionActualizada)
      );
    } catch {
      // Evita romper la pantalla si localStorage falla.
    }

    this.suscripcion = suscripcionActualizada;
    this.cerrarModalCancelacion();
    this.cargarDatos();
  }

  calcularTiempoRestante(): string {
    if (!this.suscripcion?.vencimiento) {
      return '—';
    }

    const hoy = new Date();
    const vencimiento = new Date(this.suscripcion.vencimiento);

    if (isNaN(vencimiento.getTime())) {
      return '—';
    }

    const diferenciaMs = vencimiento.getTime() - hoy.getTime();
    const dias = Math.ceil(diferenciaMs / (1000 * 60 * 60 * 24));

    if (dias <= 0) {
      return 'Vencido';
    }

    if (dias === 1) {
      return '1 día';
    }

    return `${dias} días`;
  }

  calcularProgresoSuscripcion(): number {
    if (!this.suscripcion?.fechaInicio || !this.suscripcion?.vencimiento) {
      return 0;
    }

    const inicio = new Date(this.suscripcion.fechaInicio);
    const fin = new Date(this.suscripcion.vencimiento);
    const hoy = new Date();

    if (isNaN(inicio.getTime()) || isNaN(fin.getTime())) {
      return 0;
    }

    const totalMs = fin.getTime() - inicio.getTime();
    const transcurridoMs = hoy.getTime() - inicio.getTime();

    if (totalMs <= 0) {
      return 100;
    }

    const porcentaje = Math.round((transcurridoMs / totalMs) * 100);

    return Math.min(100, Math.max(0, porcentaje));
  }

  getProgresoColor(): string {
    if (this.progresoSuscripcion >= 85) {
      return 'linear-gradient(90deg, #ef4444, #dc2626)';
    }

    if (this.progresoSuscripcion >= 60) {
      return 'linear-gradient(90deg, #f59e0b, #d97706)';
    }

    return 'linear-gradient(90deg, #6366f1, #4f46e5)';
  }

  getEstadoClass(): string {
    if (!this.suscripcion?.estado) {
      return 'estado-activa';
    }

    const estado = this.normalizarTexto(this.suscripcion.estado);

    if (estado.includes('cancelacion')) {
      return 'estado-cancelacion';
    }

    if (estado.includes('cancelada') || estado.includes('vencida')) {
      return 'estado-cancelada';
    }

    if (estado.includes('suspendida')) {
      return 'estado-suspendida';
    }

    return 'estado-activa';
  }

  getPlanClass(): string {
    if (!this.suscripcion?.plan) {
      return 'plan-esencial';
    }

    const plan = this.normalizarTexto(this.suscripcion.plan);

    if (plan.includes('premium')) {
      return 'plan-premium';
    }

    if (plan.includes('profesional')) {
      return 'plan-profesional';
    }

    return 'plan-esencial';
  }

  getTotalMensual(): number {
    return this.getPrecioMensualPlan();
  }

  getPrecioMensualPlan(): number {
    if (this.suscripcion?.precio !== undefined && this.suscripcion.precio !== null) {
      return Number(this.suscripcion.precio) || 0;
    }

    if (this.planDetalle?.precio !== undefined && this.planDetalle.precio !== null) {
      return Number(this.planDetalle.precio) || 0;
    }

    return 0;
  }

  getUsuariosMinimos(): number {
    if (
      this.suscripcion?.usuariosMinimos !== undefined &&
      this.suscripcion.usuariosMinimos !== null
    ) {
      return Number(this.suscripcion.usuariosMinimos) || 0;
    }

    if (
      this.planDetalle?.usuariosMinimos !== undefined &&
      this.planDetalle.usuariosMinimos !== null
    ) {
      return Number(this.planDetalle.usuariosMinimos) || 0;
    }

    return 0;
  }

  formatearFechaLegible(isoString: string | undefined | null): string {
    if (!isoString) {
      return '—';
    }

    const fecha = new Date(isoString);

    if (isNaN(fecha.getTime())) {
      return '—';
    }

    return fecha.toLocaleDateString('es-BO', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
    });
  }

  formatearMonto(monto: number | undefined | null, moneda: string = 'USD'): string {
    const valor = Number(monto) || 0;

    return `${moneda} ${valor.toLocaleString('es-BO', {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
    })}`;
  }

  parseLocalStorageSeguro(clave: string): any {
    try {
      const raw = localStorage.getItem(clave);

      if (raw === null || raw === undefined) {
        return null;
      }

      try {
        return JSON.parse(raw);
      } catch {
        return raw;
      }
    } catch {
      return null;
    }
  }

  normalizarTexto(valor: string | undefined | null): string {
    if (!valor) {
      return '';
    }

    return valor
      .toString()
      .trim()
      .toLowerCase()
      .normalize('NFD')
      .replace(/[\u0300-\u036f]/g, '');
  }
}