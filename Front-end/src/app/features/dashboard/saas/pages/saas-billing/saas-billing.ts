import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

import { Sidebar } from '../../../components/sidebar/sidebar';
import { Topbar } from '../../../components/topbar/topbar';

export interface Plan {
  id: string;
  nombre: string;
  precio: number;
  moneda: string;
  usuariosMinimos: number;
  ideal?: string;
  idealPara?: string;
  destacado: boolean;
  modulos: string[];
  beneficios: string[];
}

export interface Subscription {
  plan: string;
  estado: string;
  fechaInicio: string;
  vencimiento: string;
  usuariosActivos: number;
  precio: number;
  moneda: string;
  usuariosMinimos: number;
}

export interface PaymentMethod {
  tipo: string;
  ultimosCuatro: string;
  titular: string;
  marca: string;
  actualizadoEn: string;
}

export interface PaymentHistory {
  fecha: string;
  monto: number;
  plan: string;
  metodo: string;
  estado: string;
  recibo: string;
}

export interface FormularioPago {
  metodo: string;
  titular: string;
  numeroTarjeta: string;
  vencimiento: string;
  cvv: string;
}

export interface ErroresFormulario {
  metodo: string;
  titular: string;
  numeroTarjeta: string;
  vencimiento: string;
  cvv: string;
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
  selector: 'app-saas-billing',
  standalone: true,
  imports: [CommonModule, FormsModule, Sidebar, Topbar],
  templateUrl: './saas-billing.html',
  styleUrl: './saas-billing.css',
})
export class SaasBilling implements OnInit {
  modoFacturacion: string = 'new-subscription';

  procesando: boolean = false;
  confirmado: boolean = false;

  reciboGenerado: string = '';
  tituloConfirmacion: string = '';
  descripcionConfirmacion: string = '';
  mensajeBloqueoPago: string = '';

  readonly LIMITE_OPERACIONES_PLAN_MES: number = 2;

  planSeleccionado: Plan | null = null;
  suscripcionActual: Subscription | null = null;
  metodoPagoActual: PaymentMethod | null = null;
  historialPagos: PaymentHistory[] = [];
  operacionesPlanMesActual: OperacionPlanMensual[] = [];

  totalBaseMensual: number = 0;
  fechaInicioEstimada: string = '';
  fechaVencimientoEstimada: string = '';

  formulario: FormularioPago = {
    metodo: '',
    titular: '',
    numeroTarjeta: '',
    vencimiento: '',
    cvv: '',
  };

  errores: ErroresFormulario = {
    metodo: '',
    titular: '',
    numeroTarjeta: '',
    vencimiento: '',
    cvv: '',
  };

  marcaDetectada: string = '';
  ultimosCuatroPreview: string = '****';

  constructor(
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.cargarModoFacturacion();
    this.cargarPlanSeleccionado();
    this.cargarSuscripcionActual();
    this.cargarMetodoPagoActual();
    this.cargarHistorialPagos();
    this.cargarOperacionesPlanMesActual();
    this.calcularTotalBase();
    this.calcularFechas();
    this.validarBloqueosDeCompra();
  }

  cargarModoFacturacion(): void {
    const valor = this.parseLocalStorageSeguro('saas_billing_mode');

    if (
      valor === 'new-subscription' ||
      valor === 'change-plan' ||
      valor === 'update-payment-method' ||
      valor === 'completed'
    ) {
      this.modoFacturacion = valor;
      return;
    }

    this.modoFacturacion = 'new-subscription';
  }

  cargarPlanSeleccionado(): void {
    const valor = this.parseLocalStorageSeguro('saas_selected_plan');

    if (valor && typeof valor === 'object' && valor.id) {
      this.planSeleccionado = valor as Plan;
      return;
    }

    this.planSeleccionado = null;
  }

  cargarSuscripcionActual(): void {
    const valor = this.parseLocalStorageSeguro('saas_active_subscription');

    if (valor && typeof valor === 'object' && valor.plan) {
      this.suscripcionActual = valor as Subscription;
      return;
    }

    this.suscripcionActual = null;
  }

  cargarMetodoPagoActual(): void {
    const valor = this.parseLocalStorageSeguro('saas_payment_method');

    if (valor && typeof valor === 'object') {
      this.metodoPagoActual = valor as PaymentMethod;

      if (this.modoFacturacion === 'update-payment-method') {
        this.formulario.metodo = this.metodoPagoActual.tipo || '';
        this.formulario.titular = this.metodoPagoActual.titular || '';
        this.marcaDetectada = this.metodoPagoActual.marca || '';
        this.ultimosCuatroPreview =
          this.metodoPagoActual.ultimosCuatro || '****';
      }

      return;
    }

    this.metodoPagoActual = null;
  }

  cargarHistorialPagos(): void {
    const valor = this.parseLocalStorageSeguro('saas_payment_history');

    if (Array.isArray(valor)) {
      this.historialPagos = valor as PaymentHistory[];
      return;
    }

    this.historialPagos = [];
  }

  cargarOperacionesPlanMesActual(): void {
    const mesActual = this.obtenerClaveMesActual();
    const valor = this.parseLocalStorageSeguro('saas_monthly_plan_operations');

    const operacionesGuardadas = Array.isArray(valor)
      ? (valor as OperacionPlanMensual[])
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

  calcularTotalBase(): void {
    if (!this.planSeleccionado) {
      this.totalBaseMensual = 0;
      return;
    }

    this.totalBaseMensual = this.planSeleccionado.precio;
  }

  calcularFechas(): void {
    const hoy = new Date();

    this.fechaInicioEstimada = hoy.toLocaleDateString('es-BO', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
    });

    const vencimiento = this.crearFechaVencimiento(hoy);

    this.fechaVencimientoEstimada = vencimiento.toLocaleDateString('es-BO', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
    });
  }

  crearFechaVencimiento(desde: Date): Date {
    const fecha = new Date(desde);
    fecha.setDate(fecha.getDate() + 30);
    return fecha;
  }

  validarBloqueosDeCompra(): void {
    this.mensajeBloqueoPago = '';

    if (!this.esModoConPlan()) {
      return;
    }

    if (!this.planSeleccionado) {
      this.mensajeBloqueoPago =
        'No hay un plan seleccionado. Vuelve a la pantalla de planes para elegir uno.';
      return;
    }

    if (this.esMismoPlanActivo()) {
      this.mensajeBloqueoPago =
        'Este plan ya está activo en tu suscripción. No se puede realizar un nuevo cobro por el mismo plan.';
      return;
    }

    if (this.planFueCompradoEsteMes(this.planSeleccionado)) {
      this.mensajeBloqueoPago =
        'Este plan ya fue comprado o activado durante este mes. Para evitar cobros repetidos, no puedes volver a adquirirlo hasta el siguiente mes.';
      return;
    }

    if (this.superoLimiteOperacionesPlanMes()) {
      this.mensajeBloqueoPago =
        'Ya realizaste 2 compras o cambios de plan este mes. Espera al siguiente mes para adquirir otro plan o mejorar tu suscripción con más beneficios.';
      return;
    }
  }

  esMismoPlanActivo(): boolean {
    if (!this.planSeleccionado || !this.suscripcionActual) {
      return false;
    }

    const planActual = this.normalizarPlan(this.suscripcionActual.plan);
    const planNuevoNombre = this.normalizarPlan(this.planSeleccionado.nombre);
    const planNuevoId = this.normalizarPlan(this.planSeleccionado.id);

    return planActual === planNuevoNombre || planActual === planNuevoId;
  }

  planFueCompradoEsteMes(plan: Plan): boolean {
    const planNombre = this.normalizarPlan(plan.nombre);
    const planId = this.normalizarPlan(plan.id);

    return this.operacionesPlanMesActual.some((op) => {
      const opPlan = this.normalizarPlan(op.plan);
      const opPlanId = this.normalizarPlan(op.planId);

      return (
        opPlan === planNombre ||
        opPlan === planId ||
        opPlanId === planNombre ||
        opPlanId === planId
      );
    });
  }

  superoLimiteOperacionesPlanMes(): boolean {
    return (
      this.operacionesPlanMesActual.length >= this.LIMITE_OPERACIONES_PLAN_MES
    );
  }

  validarFormulario(): boolean {
    this.errores = {
      metodo: '',
      titular: '',
      numeroTarjeta: '',
      vencimiento: '',
      cvv: '',
    };

    let valido = true;

    if (!this.formulario.metodo) {
      this.errores.metodo = 'Selecciona un método de pago.';
      valido = false;
    }

    if (!this.formulario.titular || this.formulario.titular.trim().length < 3) {
      this.errores.titular = 'Ingresa el nombre del titular o empresa.';
      valido = false;
    }

    if (this.formulario.metodo === 'Tarjeta') {
      const numero = this.formulario.numeroTarjeta.replace(/\s/g, '');

      if (!numero || numero.length < 13) {
        this.errores.numeroTarjeta = 'Ingresa un número de tarjeta válido.';
        valido = false;
      }

      if (
        !this.formulario.vencimiento ||
        !this.formulario.vencimiento.match(/^\d{2}\/\d{2}$/)
      ) {
        this.errores.vencimiento = 'Ingresa la fecha en formato MM/AA.';
        valido = false;
      }

      if (!this.formulario.cvv || this.formulario.cvv.length < 3) {
        this.errores.cvv = 'Ingresa un CVV válido.';
        valido = false;
      }
    }

    return valido;
  }

  confirmarPago(): void {
    if (this.procesando || this.confirmado) {
      return;
    }

    if (this.modoFacturacion === 'update-payment-method') {
      this.guardarMetodoPago();
      return;
    }

    this.validarBloqueosDeCompra();

    if (this.mensajeBloqueoPago) {
      return;
    }

    if (!this.planSeleccionado) {
      this.mensajeBloqueoPago =
        'No hay un plan seleccionado. Vuelve a la pantalla de planes para elegir uno.';
      return;
    }

    if (!this.validarFormulario()) {
      return;
    }

    this.mensajeBloqueoPago = '';
    this.procesando = true;
    this.confirmado = false;
    this.tituloConfirmacion = 'Pago completado';
    this.descripcionConfirmacion = '';
    this.cdr.detectChanges();

    setTimeout(() => {
      try {
        this.guardarSuscripcionActiva();
        this.guardarMetodoPagoStorage();
        this.agregarPagoAlHistorial();
        this.registrarOperacionPlanMensual();
        this.guardarModoCompletado();

        this.tituloConfirmacion = 'Pago completado';
        this.descripcionConfirmacion =
          'Tu pago fue confirmado correctamente.';
      } catch (error) {
        console.error('Error durante la confirmación del pago:', error);

        this.tituloConfirmacion = 'Pago registrado';
        this.descripcionConfirmacion =
          'La operación fue procesada. Puedes revisar los cambios en Mi suscripción.';
      }

      this.procesando = false;
      this.confirmado = true;
      this.cdr.detectChanges();

      setTimeout(() => {
        this.router.navigate(['/saas']);
      }, 3000);
    }, 1800);
  }

  guardarMetodoPago(): void {
    if (this.confirmado) {
      return;
    }

    if (!this.validarFormulario()) {
      return;
    }

    try {
      this.guardarMetodoPagoStorage();

      this.procesando = false;
      this.confirmado = true;
      this.tituloConfirmacion = 'Cambio realizado';
      this.descripcionConfirmacion =
        'El método de pago fue actualizado correctamente.';

      /*
        IMPORTANTE:
        No se llama a guardarModoCompletado() aquí porque cambia
        modoFacturacion a "completed" y eso puede hacer que el HTML
        muestre "Pago completado". Para este flujo mantenemos
        modoFacturacion = "update-payment-method" hasta navegar.
      */

      this.cdr.detectChanges();

      setTimeout(() => {
        this.router.navigate(['/saas']);
      }, 1800);
    } catch (error) {
      console.error('Error al actualizar método de pago:', error);

      this.procesando = false;
      this.confirmado = false;
      this.mensajeBloqueoPago =
        'No se pudo actualizar el método de pago. Intenta nuevamente.';
      this.cdr.detectChanges();
    }
  }

  guardarSuscripcionActiva(): void {
    if (!this.planSeleccionado) {
      return;
    }

    const hoy = new Date();
    const vencimiento = this.crearFechaVencimiento(hoy);

    const usuariosActivos =
      this.suscripcionActual?.usuariosActivos ||
      this.planSeleccionado.usuariosMinimos;

    const nuevaSuscripcion: Subscription = {
      plan: this.planSeleccionado.nombre,
      estado: 'Activa',
      fechaInicio: hoy.toISOString(),
      vencimiento: vencimiento.toISOString(),
      usuariosActivos,
      precio: this.planSeleccionado.precio,
      moneda: this.planSeleccionado.moneda || 'USD',
      usuariosMinimos: this.planSeleccionado.usuariosMinimos,
    };

    try {
      localStorage.setItem(
        'saas_active_subscription',
        JSON.stringify(nuevaSuscripcion)
      );

      this.suscripcionActual = nuevaSuscripcion;
    } catch (e) {
      console.error('Error al guardar suscripción activa:', e);
    }
  }

  guardarMetodoPagoStorage(): void {
    const metodo: PaymentMethod = {
      tipo: this.formulario.metodo,
      ultimosCuatro: this.obtenerUltimosCuatro(),
      titular: this.formulario.titular.trim(),
      marca: this.detectarMarcaTarjeta(),
      actualizadoEn: new Date().toISOString(),
    };

    try {
      localStorage.setItem('saas_payment_method', JSON.stringify(metodo));
      this.metodoPagoActual = metodo;
    } catch (e) {
      console.error('Error al guardar método de pago:', e);
    }
  }

  agregarPagoAlHistorial(): void {
    if (!this.planSeleccionado) {
      return;
    }

    const recibo = this.generarRecibo();
    this.reciboGenerado = recibo;

    const nuevoPago: PaymentHistory = {
      fecha: new Date().toISOString(),
      monto: this.totalBaseMensual,
      plan: this.planSeleccionado.nombre,
      metodo: this.formulario.metodo,
      estado: 'Pagado',
      recibo,
    };

    const historialActualizado = [...this.historialPagos, nuevoPago];

    try {
      localStorage.setItem(
        'saas_payment_history',
        JSON.stringify(historialActualizado)
      );

      this.historialPagos = historialActualizado;
    } catch (e) {
      console.error('Error al guardar historial de pagos:', e);
    }
  }

  registrarOperacionPlanMensual(): void {
    if (!this.planSeleccionado) {
      return;
    }

    const ahora = new Date();
    const recibo = this.reciboGenerado || this.generarRecibo();

    const nuevaOperacion: OperacionPlanMensual = {
      fecha: ahora.toISOString(),
      mes: this.obtenerClaveMes(ahora),
      plan: this.planSeleccionado.nombre,
      planId: this.planSeleccionado.id,
      tipoOperacion:
        this.modoFacturacion === 'change-plan'
          ? 'change-plan'
          : 'new-subscription',
      monto: this.totalBaseMensual,
      moneda: this.planSeleccionado.moneda || 'USD',
      recibo,
    };

    const operacionesActualizadas = [
      ...this.operacionesPlanMesActual,
      nuevaOperacion,
    ];

    this.operacionesPlanMesActual = operacionesActualizadas;
    this.guardarOperacionesPlanStorage(operacionesActualizadas);
  }

  guardarOperacionesPlanStorage(
    operacionesDelMes: OperacionPlanMensual[]
  ): void {
    const valor = this.parseLocalStorageSeguro('saas_monthly_plan_operations');

    const operacionesPrevias = Array.isArray(valor)
      ? (valor as OperacionPlanMensual[])
      : [];

    const mesActual = this.obtenerClaveMesActual();

    const operacionesOtrosMeses = operacionesPrevias.filter(
      (op) => op.mes !== mesActual
    );

    const operacionesFinales = [...operacionesOtrosMeses, ...operacionesDelMes];

    try {
      localStorage.setItem(
        'saas_monthly_plan_operations',
        JSON.stringify(operacionesFinales)
      );
    } catch (e) {
      console.error('Error al guardar operaciones mensuales SaaS:', e);
    }
  }

  guardarModoCompletado(): void {
    try {
      localStorage.setItem('saas_billing_mode', 'completed');
      this.modoFacturacion = 'completed';
    } catch (e) {
      console.error('Error al actualizar modo de facturación:', e);
    }
  }

  volverPlanes(): void {
    this.router.navigate(['/saas/planes']);
  }

  volverSuscripcion(): void {
    this.router.navigate(['/saas']);
  }

  volverElegirPlan(): void {
    this.router.navigate(['/saas/planes']);
  }

  limpiarFormulario(): void {
    this.formulario = {
      metodo: '',
      titular: '',
      numeroTarjeta: '',
      vencimiento: '',
      cvv: '',
    };

    this.errores = {
      metodo: '',
      titular: '',
      numeroTarjeta: '',
      vencimiento: '',
      cvv: '',
    };

    this.marcaDetectada = '';
    this.ultimosCuatroPreview = '****';
  }

  onMetodoChange(): void {
    this.formulario.numeroTarjeta = '';
    this.formulario.vencimiento = '';
    this.formulario.cvv = '';

    this.errores.numeroTarjeta = '';
    this.errores.vencimiento = '';
    this.errores.cvv = '';

    this.marcaDetectada = this.detectarMarcaTarjeta();
    this.ultimosCuatroPreview = '****';
  }

  onNumeroTarjetaChange(): void {
    const limpio = this.formulario.numeroTarjeta
      .replace(/\D/g, '')
      .slice(0, 16);

    this.formulario.numeroTarjeta = limpio.replace(/(.{4})/g, '$1 ').trim();

    this.marcaDetectada = this.detectarMarcaTarjeta();
    this.ultimosCuatroPreview = this.obtenerUltimosCuatro() || '****';
  }

  onTitularChange(): void {
    this.errores.titular = '';
  }

  obtenerUltimosCuatro(): string {
    if (this.formulario.metodo !== 'Tarjeta') {
      return '';
    }

    const num = this.formulario.numeroTarjeta.replace(/\s/g, '');

    if (num.length >= 4) {
      return num.slice(-4);
    }

    return '';
  }

  detectarMarcaTarjeta(): string {
    if (this.formulario.metodo === 'PayPal') {
      return 'PayPal';
    }

    if (this.formulario.metodo === 'Transferencia') {
      return 'Transferencia';
    }

    const num = this.formulario.numeroTarjeta.replace(/\s/g, '');

    if (num.startsWith('4')) {
      return 'Visa';
    }

    if (num.startsWith('5') || num.startsWith('2')) {
      return 'Mastercard';
    }

    if (num.startsWith('3')) {
      return 'Amex';
    }

    return 'Tarjeta';
  }

  generarRecibo(): string {
    const anio = new Date().getFullYear();
    const siguiente = (this.historialPagos.length + 1)
      .toString()
      .padStart(4, '0');

    return `REC-${anio}-${siguiente}`;
  }

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

  obtenerTextoModo(): string {
    switch (this.modoFacturacion) {
      case 'new-subscription':
        return 'Nueva suscripción';

      case 'change-plan':
        return 'Cambio de plan';

      case 'update-payment-method':
        return 'Actualización de método de pago';

      case 'completed':
        return 'Completado';

      default:
        return 'Nueva suscripción';
    }
  }

  obtenerDescripcionModo(): string {
    switch (this.modoFacturacion) {
      case 'new-subscription':
        return 'Estás activando una nueva suscripción mensual para tu empresa.';

      case 'change-plan':
        return 'Estás cambiando tu plan actual por el plan seleccionado.';

      case 'update-payment-method':
        return 'Estás actualizando el método de pago registrado. No se realizará ningún cobro.';

      case 'completed':
        return 'La operación fue completada correctamente.';

      default:
        return 'Completa los datos para continuar.';
    }
  }

  obtenerColorModo(): string {
    switch (this.modoFacturacion) {
      case 'new-subscription':
        return 'badge-new';

      case 'change-plan':
        return 'badge-change';

      case 'update-payment-method':
        return 'badge-update';

      default:
        return 'badge-new';
    }
  }

  esModoConPlan(): boolean {
    return (
      this.modoFacturacion === 'new-subscription' ||
      this.modoFacturacion === 'change-plan'
    );
  }

  tieneErrores(): boolean {
    return Object.values(this.errores).some((error) => error !== '');
  }

  formatearFechaLegible(isoString: string): string {
    if (!isoString) {
      return '—';
    }

    try {
      return new Date(isoString).toLocaleDateString('es-BO', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
      });
    } catch {
      return '—';
    }
  }

  formatearMonto(monto: number, moneda: string): string {
    if (monto == null) {
      return '—';
    }

    return `${moneda || 'USD'} ${monto.toLocaleString('es-BO', {
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

  get mostrarFormularioPrincipal(): boolean {
    if (this.confirmado) {
      return false;
    }

    if (this.modoFacturacion === 'update-payment-method') {
      return true;
    }

    return this.planSeleccionado !== null && !this.mensajeBloqueoPago;
  }

  get mostrarEstadoSinPlan(): boolean {
    if (this.confirmado) {
      return false;
    }

    if (this.modoFacturacion === 'update-payment-method') {
      return false;
    }

    return this.planSeleccionado === null;
  }

  get titularPreview(): string {
    return this.formulario.titular
      ? this.formulario.titular.toUpperCase()
      : 'NOMBRE TITULAR';
  }

  get numeroEnmascarado(): string {
    if (this.formulario.metodo !== 'Tarjeta') {
      return '';
    }

    const num = this.formulario.numeroTarjeta.replace(/\s/g, '');

    if (num.length <= 4) {
      return num || '**** **** **** ****';
    }

    const visible = num.slice(-4);

    return `**** **** **** ${visible}`;
  }

  get vencimientoPreview(): string {
    return this.formulario.vencimiento || 'MM/AA';
  }

  get textoBotonPrincipal(): string {
    if (this.modoFacturacion === 'update-payment-method') {
      return 'Guardar información';
    }

    return `Pagar ${this.formatearMonto(
      this.totalBaseMensual,
      this.planSeleccionado?.moneda || 'USD'
    )}`;
  }

  get textoResumenTotal(): string {
    return this.formatearMonto(
      this.totalBaseMensual,
      this.planSeleccionado?.moneda || 'USD'
    );
  }
}