import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  ReactiveFormsModule,
  FormBuilder,
  FormGroup,
  Validators,
} from '@angular/forms';
import { Subject } from 'rxjs';
import { takeUntil, debounceTime, distinctUntilChanged } from 'rxjs/operators';

import { Sidebar } from '../../../components/sidebar/sidebar';
import { Topbar } from '../../../components/topbar/topbar';
import {
  SaasFeatureKey,
  SaasPlanService,
} from '../../../../../core/services/saas-plan.service';

export interface Turno {
  id: number;
  nombre: string;
  horaInicio: string;
  horaFin: string;
  descripcion: string;
  estado: 'Activo' | 'Inactivo';
}

@Component({
  selector: 'app-gestion-turnos',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, Sidebar, Topbar],
  templateUrl: './gestion-turnos.html',
  styleUrls: ['./gestion-turnos.css'],
})
export class GestionTurnosComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();

  listaTurnos: Turno[] = [];
  turnosFiltrados: Turno[] = [];

  modalVisible = false;
  modoModal: 'crear' | 'editar' = 'crear';
  turnoEditandoId: number | null = null;
  nextId = 4;

  bloqueoVisible = false;
  bloqueoTitulo = '';
  bloqueoMensaje = '';
  bloqueoPlanActual = '';
  bloqueoPlanRequerido = '';

  searchForm: FormGroup;
  turnoForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private saasPlanService: SaasPlanService
  ) {
    this.searchForm = this.fb.group({
      busqueda: [''],
    });

    this.turnoForm = this.fb.group({
      nombre: ['', [Validators.required, Validators.minLength(2)]],
      horaInicio: ['', Validators.required],
      horaFin: ['', Validators.required],
      descripcion: [''],
      estado: ['Activo', Validators.required],
    });
  }

  ngOnInit(): void {
    this.turnosFiltrados = [...this.listaTurnos];

    this.searchForm
      .get('busqueda')!
      .valueChanges.pipe(
        debounceTime(250),
        distinctUntilChanged(),
        takeUntil(this.destroy$)
      )
      .subscribe((valor: string) => {
        this.aplicarFiltro(valor);
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  get totalTurnos(): number {
    return this.listaTurnos.length;
  }

  get turnosActivos(): number {
    return this.listaTurnos.filter((t) => t.estado === 'Activo').length;
  }

  get turnosInactivos(): number {
    return this.listaTurnos.filter((t) => t.estado === 'Inactivo').length;
  }

  puedeUsar(feature: SaasFeatureKey): boolean {
    return this.saasPlanService.canAccess(feature);
  }

  accionBloqueada(feature: SaasFeatureKey): boolean {
    return !this.puedeUsar(feature);
  }

  aplicarFiltro(valor: string): void {
    const termino = valor?.toLowerCase().trim() ?? '';

    if (!termino) {
      this.turnosFiltrados = [...this.listaTurnos];
      return;
    }

    this.turnosFiltrados = this.listaTurnos.filter(
      (turno) =>
        turno.nombre.toLowerCase().includes(termino) ||
        turno.estado.toLowerCase().includes(termino)
    );
  }

  abrirModal(modo: 'crear' | 'editar', turno?: Turno): void {
    if (this.accionBloqueada('turnos')) {
      const titulo =
        modo === 'crear'
          ? 'Crear nuevo turno'
          : 'Editar turno laboral';

      this.mostrarBloqueo('turnos', titulo);
      return;
    }

    this.modoModal = modo;
    this.modalVisible = true;

    if (modo === 'editar' && turno) {
      this.turnoEditandoId = turno.id;

      this.turnoForm.patchValue({
        nombre: turno.nombre,
        horaInicio: turno.horaInicio,
        horaFin: turno.horaFin,
        descripcion: turno.descripcion,
        estado: turno.estado,
      });
    } else {
      this.turnoEditandoId = null;
      this.turnoForm.reset({ estado: 'Activo' });
    }
  }

  cerrarModal(): void {
    this.modalVisible = false;
    this.turnoEditandoId = null;
    this.turnoForm.reset({ estado: 'Activo' });
  }

  guardarTurno(): void {
    if (this.accionBloqueada('turnos')) {
      this.mostrarBloqueo('turnos', 'Guardar turno laboral');
      return;
    }

    if (this.turnoForm.invalid) {
      this.turnoForm.markAllAsTouched();
      return;
    }

    const valores = this.turnoForm.value;

    if (this.modoModal === 'crear') {
      const nuevoTurno: Turno = {
        id: this.nextId++,
        nombre: valores.nombre,
        horaInicio: valores.horaInicio,
        horaFin: valores.horaFin,
        descripcion: valores.descripcion ?? '',
        estado: valores.estado,
      };

      this.listaTurnos = [...this.listaTurnos, nuevoTurno];
    } else if (this.modoModal === 'editar' && this.turnoEditandoId !== null) {
      this.listaTurnos = this.listaTurnos.map((turno) =>
        turno.id === this.turnoEditandoId
          ? {
              ...turno,
              nombre: valores.nombre,
              horaInicio: valores.horaInicio,
              horaFin: valores.horaFin,
              descripcion: valores.descripcion ?? '',
              estado: valores.estado,
            }
          : turno
      );
    }

    const terminoBusqueda = this.searchForm.get('busqueda')!.value ?? '';
    this.aplicarFiltro(terminoBusqueda);
    this.cerrarModal();
  }

  cambiarEstado(id: number): void {
    if (this.accionBloqueada('turnos')) {
      this.mostrarBloqueo('turnos', 'Cambiar estado del turno');
      return;
    }

    this.listaTurnos = this.listaTurnos.map((turno) =>
      turno.id === id
        ? {
            ...turno,
            estado: turno.estado === 'Activo' ? 'Inactivo' : 'Activo',
          }
        : turno
    );

    const terminoBusqueda = this.searchForm.get('busqueda')!.value ?? '';
    this.aplicarFiltro(terminoBusqueda);
  }

  campoInvalido(campo: string): boolean {
    const control = this.turnoForm.get(campo);
    return !!(control && control.invalid && control.touched);
  }

  cerrarModalPorOverlay(event: MouseEvent): void {
    if ((event.target as HTMLElement).classList.contains('modal-overlay')) {
      this.cerrarModal();
    }
  }

  cerrarBloqueo(): void {
    this.bloqueoVisible = false;
    this.bloqueoTitulo = '';
    this.bloqueoMensaje = '';
    this.bloqueoPlanActual = '';
    this.bloqueoPlanRequerido = '';
  }

  getPlanActual(): string {
    return this.saasPlanService.getPlanLabel(
      this.saasPlanService.getActivePlan()
    );
  }

  private mostrarBloqueo(feature: SaasFeatureKey, titulo: string): void {
    const planActual = this.saasPlanService.getActivePlan();
    const planRequerido = this.saasPlanService.getRequiredPlan(feature);

    this.bloqueoTitulo = titulo;
    this.bloqueoMensaje = this.saasPlanService.getLockMessage(feature);
    this.bloqueoPlanActual = this.saasPlanService.getPlanLabel(planActual);
    this.bloqueoPlanRequerido =
      this.saasPlanService.getPlanLabel(planRequerido);
    this.bloqueoVisible = true;
  }
}