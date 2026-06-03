import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

import { Sidebar } from '../../../components/sidebar/sidebar';
import { Topbar } from '../../../components/topbar/topbar';
import { SaasService, PlanResponseDTO } from '../../../../../core/services/saas.service';

export interface Plan {
  id: string;
  nombre: string;
  precio: number;
  moneda: string;
  usuariosMinimos: number;
  ideal: string;
  idealPara?: string;
  destacado: boolean;
  modulos: string[];
  beneficios: string[];
}

export interface SuscripcionActiva {
  plan: string;
  estado: string;
  fechaInicio: string;
  vencimiento: string;
  usuariosActivos: number;
  precio?: number;
  moneda?: string;
  usuariosMinimos?: number;
}

export interface HistorialPago {
  fecha: string;
  monto: number;
  plan: string;
  metodo: string;
  estado: string;
  recibo: string;
}

export interface OperacionPlanMensual {
  fecha: string;
  mes: string;
  plan: string;
  planId: string;
  tipoOperacion: 'new-subscription' | 'change-plan';
  monto: number;
  moneda: string;
  recibo: string;
}

@Component({
  selector: 'app-saas-plans',
  standalone: true,
  imports: [CommonModule, Sidebar, Topbar],
  templateUrl: './saas-plans.html',
  styleUrls: ['./saas-plans.css'],
})
export class SaasPlans implements OnInit {
  modoFacturacion: string = 'new-subscription';

  suscripcionActual: SuscripcionActiva | null = null;
  planSeleccionado: Plan | null = null;
  cargandoPlanes: boolean = true;
  modulosExpandidos: boolean = false;

  historialPagos: HistorialPago[] = [];
  operacionesPlanMesActual: OperacionPlanMensual[] = [];

  mostrarErrorSeleccion: boolean = false;
  mensajeErrorSeleccion: string = '';

  mostrarModalCambioPlan: boolean = false;
  planPendienteCambio: Plan | null = null;

  readonly LIMITE_OPERACIONES_PLAN_MES: number = 2;

  planes: Plan[] = [];

  constructor(
    private router: Router, 
    private saasService: SaasService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.cargarSuscripcionActual();
    this.cargarModoFacturacion();
    this.cargarHistorialPagos();
    this.cargarOperacionesPlanMesActual();
    this.cargarPlanesDesdeBackend();
  }

  cargarPlanesDesdeBackend(): void {
    this.cargandoPlanes = true;
    this.cdr.detectChanges();
    
    this.saasService.getPlanesActivos().subscribe({
      next: (planesBackend: any[]) => {
        // Simulamos un tiempo de carga artificial de 1.5s
        setTimeout(() => {
          this.planes = planesBackend.map((p: any) => {
            const precio = p.precioUsd !== undefined ? p.precioUsd : p.precio_usd;
            const usuariosMinimos = p.limiteUsuarios !== undefined ? p.limiteUsuarios : p.limite_usuarios;
            
            return {
              id: p.id.toString(),
              nombre: p.nombre,
              precio: precio,
              moneda: 'USD',
              usuariosMinimos: usuariosMinimos,
              ideal: p.descripcion || 'Empresas',
              destacado: p.nombre.toLowerCase() === 'profesional',
              modulos: p.modulos ? p.modulos.map((m: any) => typeof m === 'object' ? (m.nombre || m.descripcion || 'Módulo') : m) : [],
              beneficios: p.beneficios ? p.beneficios.map((b: any) => typeof b === 'object' ? (b.nombre || b.descripcion || 'Beneficio') : b) : []
            };
          });
          this.cargandoPlanes = false;
          this.cargarPlanSeleccionado(); // Aseguramos que se cargue después de tener la lista
          this.cdr.detectChanges();
        }, 1500);
      },
      error: (err: any) => {
        console.error('Error al cargar planes del backend', err);
        this.cargandoPlanes = false;
        this.cdr.detectChanges();
      }
    });
  }

  /* ═══════════════════════════════════════════════════════
     CARGA DE DATOS
  ═══════════════════════════════════════════════════════ */

  cargarModoFacturacion(): void {
    const modo = this.parseLocalStorageSeguro('saas_billing_mode');

    if (this.suscripcionActual && modo !== 'update-payment-method') {
      this.modoFacturacion = 'change-plan';
      this.guardarModoFacturacion('change-plan');
      return;
    }

    if (modo && typeof modo === 'string') {
      if (
        modo === 'new-subscription' ||
        modo === 'change-plan' ||
        modo === 'update-payment-method' ||
        modo === 'completed'
      ) {
        this.modoFacturacion = modo;
        return;
      }
    }

    this.modoFacturacion = 'new-subscription';
    this.guardarModoFacturacion('new-subscription');
  }

  cargarSuscripcionActual(): void {
    const raw = this.parseLocalStorageSeguro('saas_active_subscription');

    if (raw && typeof raw === 'object' && raw.plan && raw.estado) {
      this.suscripcionActual = raw as SuscripcionActiva;
      return;
    }

    this.suscripcionActual = null;
  }

  cargarHistorialPagos(): void {
    const raw = this.parseLocalStorageSeguro('saas_payment_history');

    if (Array.isArray(raw)) {
      this.historialPagos = raw as HistorialPago[];
      return;
    }

    this.historialPagos = [];
  }

  cargarOperacionesPlanMesActual(): void {
    const mesActual = this.obtenerClaveMesActual();
    const raw = this.parseLocalStorageSeguro('saas_monthly_plan_operations');

    const operacionesGuardadas = Array.isArray(raw)
      ? (raw as OperacionPlanMensual[])
      : [];

    const operacionesDelMes = operacionesGuardadas.filter(
      (op) => op.mes === mesActual
    );

    const operacionesDesdeHistorial =
      this.obtenerOperacionesDesdeHistorialDelMes();

    const mapaOperaciones = new Map<string, OperacionPlanMensual>();

    for (const op of operacionesDelMes) {
      const clave = op.recibo || `${op.plan}-${op.fecha}`;
      mapaOperaciones.set(clave, op);
    }

    for (const op of operacionesDesdeHistorial) {
      const clave = op.recibo || `${op.plan}-${op.fecha}`;

      if (!mapaOperaciones.has(clave)) {
        mapaOperaciones.set(clave, op);
      }
    }

    this.operacionesPlanMesActual = Array.from(mapaOperaciones.values());

    this.guardarOperacionesPlanStorage(this.operacionesPlanMesActual);
  }

  cargarPlanSeleccionado(): void {
    const raw = this.parseLocalStorageSeguro('saas_selected_plan');

    if (raw && typeof raw === 'object' && raw.id) {
      const encontrado = this.planes.find((p) => p.id === raw.id);

      if (encontrado && !this.estaPlanBloqueado(encontrado)) {
        this.planSeleccionado = encontrado;
        return;
      }

      this.limpiarPlanSeleccionadoStorage();
    }

    this.planSeleccionado = null;
  }

  obtenerOperacionesDesdeHistorialDelMes(): OperacionPlanMensual[] {
    const mesActual = this.obtenerClaveMesActual();

    return this.historialPagos
      .filter((pago) => {
        if (!pago.fecha || !pago.plan) {
          return false;
        }

        const fechaPago = new Date(pago.fecha);

        if (isNaN(fechaPago.getTime())) {
          return false;
        }

        return this.obtenerClaveMes(fechaPago) === mesActual;
      })
      .map((pago) => {
        return {
          fecha: pago.fecha,
          mes: mesActual,
          plan: pago.plan,
          planId: this.normalizarPlan(pago.plan),
          tipoOperacion: 'change-plan',
          monto: Number(pago.monto) || 0,
          moneda: 'USD',
          recibo: pago.recibo || '',
        };
      });
  }

  /* ═══════════════════════════════════════════════════════
     SELECCIÓN DE PLAN
  ═══════════════════════════════════════════════════════ */

  seleccionarPlan(plan: Plan): void {
    const motivoBloqueo = this.obtenerMotivoBloqueoPlan(plan);

    if (motivoBloqueo) {
      this.planSeleccionado = null;
      this.mostrarErrorSeleccion = true;
      this.mensajeErrorSeleccion = motivoBloqueo;
      return;
    }

    this.planSeleccionado = plan;
    this.mostrarErrorSeleccion = false;
    this.mensajeErrorSeleccion = '';
  }

  continuarFacturacion(): void {
    if (!this.planSeleccionado) {
      this.mostrarErrorSeleccion = true;
      this.mensajeErrorSeleccion = 'Selecciona un plan antes de continuar.';
      return;
    }

    const motivoBloqueo = this.obtenerMotivoBloqueoPlan(this.planSeleccionado);

    if (motivoBloqueo) {
      this.mostrarErrorSeleccion = true;
      this.mensajeErrorSeleccion = motivoBloqueo;
      this.planSeleccionado = null;
      this.limpiarPlanSeleccionadoStorage();
      return;
    }

    this.mostrarErrorSeleccion = false;
    this.mensajeErrorSeleccion = '';

    if (this.suscripcionActual) {
      this.modoFacturacion = 'change-plan';
      this.planPendienteCambio = this.planSeleccionado;
      this.mostrarModalCambioPlan = true;
      return;
    }

    this.modoFacturacion = 'new-subscription';
    this.guardarPlanYContinuar(this.planSeleccionado);
  }

  guardarPlanYContinuar(plan: Plan): void {
    try {
      localStorage.setItem('saas_selected_plan', JSON.stringify(plan));
      localStorage.setItem(
        'saas_billing_mode',
        this.modoFacturacion || 'new-subscription'
      );
    } catch {
      // Evita romper la navegación si localStorage falla.
    }

    this.router.navigate(['/saas/facturacion']);
  }

  cerrarModalCambioPlan(): void {
    this.mostrarModalCambioPlan = false;
    this.planPendienteCambio = null;
  }

  confirmarCambioPlan(): void {
    if (!this.planPendienteCambio) {
      return;
    }

    const motivoBloqueo = this.obtenerMotivoBloqueoPlan(
      this.planPendienteCambio
    );

    if (motivoBloqueo) {
      this.mostrarModalCambioPlan = false;
      this.planPendienteCambio = null;
      this.planSeleccionado = null;
      this.mostrarErrorSeleccion = true;
      this.mensajeErrorSeleccion = motivoBloqueo;
      return;
    }

    this.modoFacturacion = 'change-plan';
    this.guardarPlanYContinuar(this.planPendienteCambio);
  }

  volverSuscripcion(): void {
    this.router.navigate(['/saas']);
  }

  /* ═══════════════════════════════════════════════════════
     BLOQUEOS DE NEGOCIO
  ═══════════════════════════════════════════════════════ */

  obtenerMotivoBloqueoPlan(plan: Plan): string {
    if (this.esPlanActual(plan)) {
      return 'Este plan ya está activo en tu suscripción actual. No puedes volver a comprar el mismo plan.';
    }

    if (this.planFueCompradoEsteMes(plan)) {
      return 'Este plan ya fue comprado o activado durante este mes. Para evitar cobros repetidos, no puedes volver a adquirirlo hasta el siguiente mes.';
    }

    if (this.superoLimiteOperacionesPlanMes()) {
      return 'Ya realizaste 2 compras o cambios de plan este mes. Espera al siguiente mes para adquirir otro plan o mejorar tu suscripción con más beneficios.';
    }

    return '';
  }

  estaPlanBloqueado(plan: Plan): boolean {
    return this.obtenerMotivoBloqueoPlan(plan) !== '';
  }

  planEstaBloqueado(plan: Plan): boolean {
    return this.estaPlanBloqueado(plan);
  }

  esPlanActual(plan: Plan): boolean {
    if (!this.suscripcionActual) {
      return false;
    }

    const actual = this.normalizarPlan(this.suscripcionActual.plan);
    const planNombre = this.normalizarPlan(plan.nombre);
    const planId = this.normalizarPlan(plan.id);

    return actual === planNombre || actual === planId;
  }

  planFueCompradoEsteMes(plan: Plan): boolean {
    return false; // Límite removido a pedido del usuario para pruebas
  }

  superoLimiteOperacionesPlanMes(): boolean {
    return false; // Límite removido a pedido del usuario para pruebas
  }

  obtenerEstadoBloqueoPlan(plan: Plan): string {
    if (this.esPlanActual(plan)) {
      return 'Plan actual';
    }

    if (this.planFueCompradoEsteMes(plan)) {
      return 'Comprado este mes';
    }

    if (this.superoLimiteOperacionesPlanMes()) {
      return 'Límite mensual alcanzado';
    }

    return '';
  }

  obtenerTextoBotonPlan(plan: Plan): string {
    if (this.esPlanActual(plan)) {
      return 'Plan actual';
    }

    if (this.planFueCompradoEsteMes(plan)) {
      return 'Ya comprado este mes';
    }

    if (this.superoLimiteOperacionesPlanMes()) {
      return 'Bloqueado este mes';
    }

    if (this.suscripcionActual) {
      return 'Cambiar a este plan';
    }

    return 'Seleccionar plan';
  }

  /* ═══════════════════════════════════════════════════════
     UTILIDADES DE VISTA
  ═══════════════════════════════════════════════════════ */

  toggleModulos(event: Event): void {
    event.stopPropagation();
    this.modulosExpandidos = !this.modulosExpandidos;
  }

  esPlanSeleccionado(plan: Plan): boolean {
    if (!this.planSeleccionado) {
      return false;
    }

    return this.planSeleccionado.id === plan.id;
  }

  calcularTotalBase(plan: Plan): number {
    return plan.precio;
  }

  obtenerTextoModo(): string {
    if (this.modoFacturacion === 'change-plan') {
      return 'Estás cambiando tu plan actual.';
    }

    if (this.modoFacturacion === 'update-payment-method') {
      return 'Estás actualizando tu método de pago.';
    }

    return 'Estás adquiriendo una nueva suscripción.';
  }

  obtenerIconoModo(): string {
    if (this.modoFacturacion === 'change-plan') {
      return 'change';
    }

    if (this.modoFacturacion === 'update-payment-method') {
      return 'payment';
    }

    return 'new';
  }

  getPlanClaseColor(plan: Plan): string {
    if (plan.id === 'esencial') {
      return 'plan-color-esencial';
    }

    if (plan.id === 'profesional') {
      return 'plan-color-profesional';
    }

    if (plan.id === 'premium') {
      return 'plan-color-premium';
    }

    return '';
  }

  getModulosPrincipales(plan: Plan): string[] {
    return plan.modulos.slice(0, 4);
  }

  getOperacionesRestantesMes(): number {
    const restantes =
      this.LIMITE_OPERACIONES_PLAN_MES - this.operacionesPlanMesActual.length;

    return Math.max(0, restantes);
  }

  getResumenOperacionesMes(): string {
    const usadas = this.operacionesPlanMesActual.length;
    const restantes = this.getOperacionesRestantesMes();

    if (usadas === 0) {
      return 'Aún no realizaste compras o cambios de plan este mes.';
    }

    if (restantes === 0) {
      return 'Ya alcanzaste el límite mensual de 2 compras o cambios de plan.';
    }

    return `Realizaste ${usadas} operación(es) este mes. Te queda(n) ${restantes}.`;
  }

  /* ═══════════════════════════════════════════════════════
     LOCALSTORAGE
  ═══════════════════════════════════════════════════════ */

  guardarModoFacturacion(modo: string): void {
    try {
      localStorage.setItem('saas_billing_mode', modo);
    } catch {
      // Evita romper la pantalla si localStorage falla.
    }
  }

  guardarOperacionesPlanStorage(
    operacionesDelMes: OperacionPlanMensual[]
  ): void {
    const raw = this.parseLocalStorageSeguro('saas_monthly_plan_operations');

    const operacionesPrevias = Array.isArray(raw)
      ? (raw as OperacionPlanMensual[])
      : [];

    const mesActual = this.obtenerClaveMesActual();

    const operacionesOtrosMeses = operacionesPrevias.filter(
      (op) => op.mes !== mesActual
    );

    const operacionesFinales = [
      ...operacionesOtrosMeses,
      ...operacionesDelMes,
    ];

    try {
      localStorage.setItem(
        'saas_monthly_plan_operations',
        JSON.stringify(operacionesFinales)
      );
    } catch {
      // Evita romper la pantalla si localStorage falla.
    }
  }

  limpiarPlanSeleccionadoStorage(): void {
    try {
      localStorage.removeItem('saas_selected_plan');
    } catch {
      // Evita romper la pantalla si localStorage falla.
    }
  }

  parseLocalStorageSeguro(clave: string): any {
    try {
      const valor = localStorage.getItem(clave);

      if (valor === null || valor === undefined) {
        return null;
      }

      try {
        return JSON.parse(valor);
      } catch {
        return valor;
      }
    } catch {
      return null;
    }
  }

  /* ═══════════════════════════════════════════════════════
     FECHAS Y NORMALIZACIÓN
  ═══════════════════════════════════════════════════════ */

  obtenerClaveMesActual(): string {
    return this.obtenerClaveMes(new Date());
  }

  obtenerClaveMes(fecha: Date): string {
    const anio = fecha.getFullYear();
    const mes = String(fecha.getMonth() + 1).padStart(2, '0');

    return `${anio}-${mes}`;
  }

  normalizarPlan(valor: string | undefined | null): string {
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