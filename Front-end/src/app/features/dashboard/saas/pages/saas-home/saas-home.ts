import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

import { Sidebar } from '../../../components/sidebar/sidebar';
import { Topbar } from '../../../components/topbar/topbar';
import { SaasService } from '../../../../../core/services/saas.service';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

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
  cargandoDatos: boolean = true;

  constructor(
    private router: Router, 
    private saasService: SaasService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.cargarDatos();
  }

  cargarDatos(): void {
    this.cargandoDatos = true;
    this.cdr.detectChanges();

    // Simulamos un retraso para el feedback visual
    setTimeout(() => {
      forkJoin({
        suscripcion: this.saasService.obtenerSuscripcionActual().pipe(catchError(() => of(null))),
        metodosPago: this.saasService.listarMetodosPago().pipe(catchError(() => of([]))),
        historial: this.saasService.listarHistorialPagos().pipe(catchError(() => of([])))
      }).subscribe({
        next: (res: any) => {
          let mockLocal = this.parseLocalStorageSeguro('saas_active_subscription');
          let suscripcionValida = null;
          let esMock = false;

          // PRIORIDAD 1: Si hay un mock local (porque el usuario compró en esta sesión simulada), lo usamos.
          if (mockLocal && mockLocal.plan) {
            suscripcionValida = mockLocal;
            esMock = true;
          } 
          // PRIORIDAD 2: Si el backend retorna una suscripción válida, la usamos.
          else if (res.suscripcion && (res.suscripcion.plan || res.suscripcion.planNombreSnapshot)) {
            suscripcionValida = res.suscripcion;
            esMock = false;
          } 
          // PRIORIDAD 3: Fallback a un plan Gratis simulado
          else {
            mockLocal = {
              plan: 'Gratis',
              estado: 'Prueba',
              fechaInicio: new Date().toISOString(),
              vencimiento: new Date(new Date().setMonth(new Date().getMonth() + 1)).toISOString(),
              usuariosActivos: 5,
              precio: 0,
              moneda: 'USD',
              usuariosMinimos: 5
            };
            try {
              localStorage.setItem('saas_active_subscription', JSON.stringify(mockLocal));
            } catch {}
            suscripcionValida = mockLocal;
            esMock = true;
          }

          if (suscripcionValida) {
            this.suscripcion = {
              plan: esMock ? suscripcionValida.plan : (suscripcionValida.plan || suscripcionValida.planNombreSnapshot),
              estado: suscripcionValida.estado,
              fechaInicio: suscripcionValida.fechaInicio,
              vencimiento: esMock ? suscripcionValida.vencimiento : suscripcionValida.fechaFin,
              usuariosActivos: esMock ? suscripcionValida.usuariosActivos : (suscripcionValida.limiteUsuarios || suscripcionValida.limite_usuarios),
              precio: esMock ? suscripcionValida.precio : (suscripcionValida.precioMensualUsd || suscripcionValida.precio_mensual_usd),
              moneda: 'USD',
              usuariosMinimos: esMock ? suscripcionValida.usuariosMinimos : (suscripcionValida.limiteUsuarios || suscripcionValida.limite_usuarios)
            };

            // Obtener el detalle del plan para cargar módulos y beneficios
            if (esMock) {
              this.saasService.getPlanesActivos().subscribe({
                next: (planes: any[]) => {
                  const planEncontrado = planes.find((p: any) => 
                    p.nombre.toLowerCase() === this.suscripcion!.plan.toLowerCase()
                  ) || planes[0]; // Fallback al primero si no coincide exacto

                  if (planEncontrado) {
                    this.mapearPlanDetalle(planEncontrado);
                  }
                }
              });
            } else {
              this.saasService.obtenerPlan(suscripcionValida.planId).subscribe({
                next: (plan: any) => this.mapearPlanDetalle(plan),
                error: () => { this.planDetalle = null; }
              });
            }
          } else {
            this.suscripcion = null;
            this.planDetalle = null;
          }

          if (res.metodosPago && res.metodosPago.length > 0) {
            const principal = res.metodosPago.find((m: any) => m.esPrincipal) || res.metodosPago[0];
            this.metodoPago = {
              tipo: principal.metodoPagoNombre,
              ultimosCuatro: principal.ultimosDigitos,
              titular: principal.titular,
              marca: principal.marca,
              actualizadoEn: principal.createdAt
            };
          } else {
            // Mock metodo de pago if none
            this.metodoPago = {
              tipo: 'Tarjeta',
              ultimosCuatro: '4242',
              titular: 'Empresa Demo',
              marca: 'Visa',
              actualizadoEn: new Date().toISOString()
            };
          }

          if (res.historial) {
            this.historialPagos = res.historial.map((p: any) => ({
              fecha: p.fechaPago,
              monto: p.montoBase,
              plan: p.suscripcionId.toString(),
              metodo: p.metodoPagoNombre,
              estado: p.estado,
              recibo: p.referenciaTransaccion
            }));
          } else {
            this.historialPagos = [];
          }

          this.tiempoRestante = this.calcularTiempoRestante();
          this.progresoSuscripcion = this.calcularProgresoSuscripcion();
          this.cargandoDatos = false;
          this.cdr.detectChanges();
        },
        error: (err: any) => {
          console.error('Error al cargar datos SaaS', err);
          this.cargandoDatos = false;
          this.cdr.detectChanges();
        }
      });
    }, 1500);
  }

  mapearPlanDetalle(plan: any): void {
    const precio = plan.precioUsd !== undefined ? plan.precioUsd : plan.precio_usd;
    const usuariosMinimos = plan.limiteUsuarios !== undefined ? plan.limiteUsuarios : plan.limite_usuarios;
    
    this.planDetalle = {
      id: plan.id.toString(),
      nombre: plan.nombre,
      precio: precio,
      moneda: 'USD',
      usuariosMinimos: usuariosMinimos,
      ideal: plan.descripcion || '',
      modulos: plan.modulos ? plan.modulos.map((m: any) => typeof m === 'object' ? (m.nombre || m.descripcion || 'Módulo') : m) : [],
      beneficios: plan.beneficios ? plan.beneficios.map((b: any) => typeof b === 'object' ? (b.nombre || b.descripcion || 'Beneficio') : b) : []
    };
    
    // Actualizar fallback si no vino en suscripción
    if (this.suscripcion && !this.suscripcion.precio) {
      this.suscripcion.precio = precio;
    }
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
    // Al no tener un endpoint de cancelacion real por ahora en backend
    // simplemente cerramos el modal, u opcionalmente solo ocultamos la UI.
    this.cerrarModalCancelacion();
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