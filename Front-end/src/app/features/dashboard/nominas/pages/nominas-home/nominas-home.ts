import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { Sidebar } from '../../../components/sidebar/sidebar';
import { Topbar } from '../../../components/topbar/topbar';

type EstadoNomina = 'Pendiente' | 'Calculada' | 'Aprobada' | 'Pagada';
type AccionNomina = 'calcular' | 'aprobar' | 'pagar' | 'boleta' | 'exportar';

interface NominaForm {
  empleado: string;
  ci: string;
  cargo: string;
  departamento: string;
  periodo: string;
  salarioBase: number | null;
  bono: number | null;
  descuentos: number | null;
  cuentaBancaria: string;
  observacion: string;
}

interface NominaRegistro {
  id: string;
  empleado: string;
  ci: string;
  cargo: string;
  departamento: string;
  periodo: string;
  salarioBase: number;
  bono: number;
  aporteLaboral: number;
  descuentos: number;
  totalIngresos: number;
  totalDescuentos: number;
  liquidoPagable: number;
  cuentaBancaria: string;
  estado: EstadoNomina;
  fechaCalculo: string;
  aprobadoPor?: string;
  fechaAprobacion?: string;
  fechaPago?: string;
  observacion: string;
}

interface BloqueoPlan {
  visible: boolean;
  titulo: string;
  accion: string;
  mensaje: string;
  planActual: string;
  planRequerido: string;
}

@Component({
  selector: 'app-nominas-home',
  standalone: true,
  imports: [CommonModule, FormsModule, Sidebar, Topbar],
  templateUrl: './nominas-home.html',
  styleUrl: './nominas-home.css',
})
export class NominasHome implements OnInit {
  private readonly localStoragePlanKey = 'saas_active_subscription';

  activePlanRaw = 'Plan básico';
  activePlanLevel = 1;

  validationMessage = '';
  toastMessage = '';

  modalDetalleVisible = false;
  nominaSeleccionada: NominaRegistro | null = null;

  bloqueoPlan: BloqueoPlan = {
    visible: false,
    titulo: '',
    accion: '',
    mensaje: '',
    planActual: '',
    planRequerido: '',
  };

  form: NominaForm = this.getFormularioInicial();

  registros: NominaRegistro[] = [
    {
      id: 'NOM-2026-001',
      empleado: 'María Fernanda Rojas',
      ci: '7845123 SC',
      cargo: 'Analista de RRHH',
      departamento: 'Recursos Humanos',
      periodo: 'Junio 2026',
      salarioBase: 4200,
      bono: 250,
      aporteLaboral: 533.82,
      descuentos: 80,
      totalIngresos: 4450,
      totalDescuentos: 613.82,
      liquidoPagable: 3836.18,
      cuentaBancaria: '100-245-8891',
      estado: 'Aprobada',
      fechaCalculo: '03/06/2026 09:15',
      aprobadoPor: 'Administrador RRHH',
      fechaAprobacion: '03/06/2026 10:00',
      observacion: 'Nómina calculada con bono de puntualidad.',
    },
    {
      id: 'NOM-2026-002',
      empleado: 'Carlos Méndez',
      ci: '9123345 SC',
      cargo: 'Auxiliar Administrativo',
      departamento: 'Administración',
      periodo: 'Junio 2026',
      salarioBase: 3100,
      bono: 150,
      aporteLaboral: 394.01,
      descuentos: 50,
      totalIngresos: 3250,
      totalDescuentos: 444.01,
      liquidoPagable: 2805.99,
      cuentaBancaria: '200-908-4412',
      estado: 'Calculada',
      fechaCalculo: '03/06/2026 09:40',
      observacion: 'Pendiente de aprobación por responsable.',
    },
    {
      id: 'NOM-2026-003',
      empleado: 'Lucía Vargas',
      ci: '6654892 SC',
      cargo: 'Supervisora de Turnos',
      departamento: 'Operaciones',
      periodo: 'Mayo 2026',
      salarioBase: 5000,
      bono: 400,
      aporteLaboral: 635.5,
      descuentos: 120,
      totalIngresos: 5400,
      totalDescuentos: 755.5,
      liquidoPagable: 4644.5,
      cuentaBancaria: '300-112-7789',
      estado: 'Pagada',
      fechaCalculo: '30/05/2026 16:20',
      aprobadoPor: 'Administrador RRHH',
      fechaAprobacion: '30/05/2026 17:00',
      fechaPago: '31/05/2026 09:30',
      observacion: 'Pago procesado correctamente.',
    },
  ];

  ngOnInit(): void {
    this.cargarPlanActivo();
  }

  get totalNominas(): number {
    return this.registros.length;
  }

  get totalPendientes(): number {
    return this.registros.filter(
      (item) => item.estado === 'Pendiente' || item.estado === 'Calculada'
    ).length;
  }

  get totalAprobadas(): number {
    return this.registros.filter((item) => item.estado === 'Aprobada').length;
  }

  get totalLiquido(): number {
    return this.registros.reduce((total, item) => total + item.liquidoPagable, 0);
  }

  get planVisual(): string {
    return this.activePlanRaw || 'Plan básico';
  }

  get puedeCalcular(): boolean {
    return this.puedeEjecutar('calcular');
  }

  get puedeAprobar(): boolean {
    return this.puedeEjecutar('aprobar');
  }

  get puedePagar(): boolean {
    return this.puedeEjecutar('pagar');
  }

  get puedeEmitirBoleta(): boolean {
    return this.puedeEjecutar('boleta');
  }

  get puedeExportarBanco(): boolean {
    return this.puedeEjecutar('exportar');
  }

  cargarPlanActivo(): void {
    const rawPlan = localStorage.getItem(this.localStoragePlanKey);

    if (!rawPlan) {
      this.activePlanRaw = 'Plan básico';
      this.activePlanLevel = 1;
      return;
    }

    try {
      const parsed = JSON.parse(rawPlan);

      const possibleName =
        parsed?.nombre ||
        parsed?.name ||
        parsed?.plan ||
        parsed?.subscription ||
        parsed?.tipo ||
        parsed?.descripcion ||
        rawPlan;

      this.activePlanRaw = String(possibleName);
      this.activePlanLevel = this.obtenerNivelPlan(this.activePlanRaw);
    } catch {
      this.activePlanRaw = rawPlan;
      this.activePlanLevel = this.obtenerNivelPlan(rawPlan);
    }
  }

  refrescarPlan(): void {
    this.cargarPlanActivo();
    this.mostrarToast('Plan SaaS actualizado correctamente.');
  }

  registrarNomina(): void {
    this.validationMessage = '';

    if (!this.validarPlan('calcular')) return;
    if (!this.validarFormulario()) return;

    const salarioBase = Number(this.form.salarioBase);
    const bono = Number(this.form.bono || 0);
    const descuentos = Number(this.form.descuentos || 0);

    const aporteLaboral = this.redondear(salarioBase * 0.1271);
    const totalIngresos = this.redondear(salarioBase + bono);
    const totalDescuentos = this.redondear(aporteLaboral + descuentos);
    const liquidoPagable = this.redondear(totalIngresos - totalDescuentos);

    const nuevoRegistro: NominaRegistro = {
      id: this.generarCodigoNomina(),
      empleado: this.form.empleado.trim(),
      ci: this.form.ci.trim(),
      cargo: this.form.cargo.trim(),
      departamento: this.form.departamento.trim(),
      periodo: this.form.periodo.trim(),
      salarioBase,
      bono,
      aporteLaboral,
      descuentos,
      totalIngresos,
      totalDescuentos,
      liquidoPagable,
      cuentaBancaria: this.form.cuentaBancaria.trim(),
      estado: 'Calculada',
      fechaCalculo: this.obtenerFechaActual(),
      observacion: this.form.observacion.trim() || 'Sin observaciones registradas.',
    };

    this.registros = [nuevoRegistro, ...this.registros];
    this.limpiarFormulario();
    this.mostrarToast('Nómina calculada y registrada correctamente.');
  }

  limpiarFormulario(): void {
    this.form = this.getFormularioInicial();
    this.validationMessage = '';
  }

  verDetalle(registro: NominaRegistro): void {
    this.nominaSeleccionada = registro;
    this.modalDetalleVisible = true;
  }

  cerrarDetalle(): void {
    this.modalDetalleVisible = false;
    this.nominaSeleccionada = null;
  }

  aprobarNomina(registro: NominaRegistro): void {
    if (!this.validarPlan('aprobar')) return;

    if (registro.estado === 'Pagada') {
      this.mostrarToast('Esta nómina ya fue pagada.');
      return;
    }

    registro.estado = 'Aprobada';
    registro.aprobadoPor = 'Administrador RRHH';
    registro.fechaAprobacion = this.obtenerFechaActual();

    this.mostrarToast(`Nómina ${registro.id} aprobada correctamente.`);
  }

  pagarNomina(registro: NominaRegistro): void {
    if (!this.validarPlan('pagar')) return;

    if (registro.estado !== 'Aprobada') {
      this.mostrarToast('Para pagar una nómina primero debe estar aprobada.');
      return;
    }

    registro.estado = 'Pagada';
    registro.fechaPago = this.obtenerFechaActual();

    this.mostrarToast(`Pago registrado para la nómina ${registro.id}.`);
  }

  emitirBoleta(registro: NominaRegistro): void {
    if (!this.validarPlan('boleta')) return;

    this.nominaSeleccionada = registro;
    this.modalDetalleVisible = true;

    this.mostrarToast(`Boleta preparada para ${registro.empleado}.`);
  }

  exportarBanco(): void {
    if (!this.validarPlan('exportar')) return;

    const nominasAprobadas = this.registros.filter(
      (item) => item.estado === 'Aprobada'
    );

    if (nominasAprobadas.length === 0) {
      this.mostrarToast('No hay nóminas aprobadas pendientes para exportar.');
      return;
    }

    this.mostrarToast(
      `Archivo bancario generado para ${nominasAprobadas.length} nómina(s).`
    );
  }

  cerrarBloqueoPlan(): void {
    this.bloqueoPlan.visible = false;
  }

  getEstadoClass(estado: EstadoNomina): string {
    return {
      Pendiente: 'estado-pendiente',
      Calculada: 'estado-calculada',
      Aprobada: 'estado-aprobada',
      Pagada: 'estado-pagada',
    }[estado];
  }

  private validarFormulario(): boolean {
    if (!this.form.empleado.trim()) {
      this.validationMessage = 'Ingresa el nombre del empleado.';
      return false;
    }

    if (!this.form.ci.trim()) {
      this.validationMessage = 'Ingresa el CI del empleado.';
      return false;
    }

    if (!this.form.cargo.trim()) {
      this.validationMessage = 'Ingresa el cargo del empleado.';
      return false;
    }

    if (!this.form.departamento.trim()) {
      this.validationMessage = 'Ingresa el departamento.';
      return false;
    }

    if (!this.form.periodo.trim()) {
      this.validationMessage = 'Ingresa el periodo de la nómina.';
      return false;
    }

    if (!this.form.salarioBase || Number(this.form.salarioBase) <= 0) {
      this.validationMessage = 'Ingresa un salario base válido.';
      return false;
    }

    if (!this.form.cuentaBancaria.trim()) {
      this.validationMessage = 'Ingresa la cuenta bancaria del empleado.';
      return false;
    }

    return true;
  }

  private validarPlan(accion: AccionNomina): boolean {
    if (this.puedeEjecutar(accion)) return true;

    this.bloqueoPlan = {
      visible: true,
      titulo: 'Acción bloqueada por plan SaaS',
      accion: this.obtenerTextoAccion(accion),
      planActual: this.planVisual,
      planRequerido: this.obtenerPlanRequerido(accion),
      mensaje:
        'Puedes ingresar al módulo de Nóminas y revisar la información, pero esta acción está restringida por el plan activo de la empresa.',
    };

    return false;
  }

  private puedeEjecutar(accion: AccionNomina): boolean {
    const nivelRequerido = this.obtenerNivelRequeridoPorAccion(accion);
    return this.activePlanLevel >= nivelRequerido;
  }

  private obtenerNivelRequeridoPorAccion(accion: AccionNomina): number {
    const reglas: Record<AccionNomina, number> = {
      calcular: 2,
      aprobar: 2,
      boleta: 2,
      pagar: 3,
      exportar: 3,
    };

    return reglas[accion];
  }

  private obtenerPlanRequerido(accion: AccionNomina): string {
    const nivel = this.obtenerNivelRequeridoPorAccion(accion);

    if (nivel >= 3) return 'Plan empresarial';
    if (nivel >= 2) return 'Plan profesional';

    return 'Plan básico';
  }

  private obtenerTextoAccion(accion: AccionNomina): string {
    const textos: Record<AccionNomina, string> = {
      calcular: 'Calcular nómina',
      aprobar: 'Aprobar nómina',
      pagar: 'Registrar pago',
      boleta: 'Emitir boleta',
      exportar: 'Exportar banco',
    };

    return textos[accion];
  }

  private obtenerNivelPlan(plan: string): number {
    const normalizado = this.normalizarTexto(plan);

    if (
      normalizado.includes('empresarial') ||
      normalizado.includes('empresa') ||
      normalizado.includes('enterprise') ||
      normalizado.includes('premium') ||
      normalizado.includes('corporativo')
    ) {
      return 3;
    }

    if (
      normalizado.includes('profesional') ||
      normalizado.includes('pro') ||
      normalizado.includes('standard') ||
      normalizado.includes('estandar') ||
      normalizado.includes('plus')
    ) {
      return 2;
    }

    if (normalizado.includes('basico') || normalizado.includes('basic')) {
      return 1;
    }

    if (
      normalizado.includes('gratis') ||
      normalizado.includes('free') ||
      normalizado.includes('demo') ||
      normalizado.includes('prueba')
    ) {
      return 0;
    }

    return 1;
  }

  private normalizarTexto(texto: string): string {
    return texto
      .toLowerCase()
      .normalize('NFD')
      .replace(/[\u0300-\u036f]/g, '')
      .trim();
  }

  private getFormularioInicial(): NominaForm {
    return {
      empleado: '',
      ci: '',
      cargo: '',
      departamento: '',
      periodo: 'Junio 2026',
      salarioBase: null,
      bono: 0,
      descuentos: 0,
      cuentaBancaria: '',
      observacion: '',
    };
  }

  private generarCodigoNomina(): string {
    const correlativo = String(this.registros.length + 1).padStart(3, '0');
    return `NOM-2026-${correlativo}`;
  }

  private obtenerFechaActual(): string {
    return new Date().toLocaleString('es-BO', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  }

  private redondear(valor: number): number {
    return Math.round(valor * 100) / 100;
  }

  private mostrarToast(mensaje: string): void {
    this.toastMessage = mensaje;

    setTimeout(() => {
      this.toastMessage = '';
    }, 3500);
  }
}