import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';

import { Sidebar } from '../../../components/sidebar/sidebar';
import { Topbar } from '../../../components/topbar/topbar';
import { Usuario, UsuariosService } from '../../../../../core/services/usuarios.service';
import {
  SaasFeatureKey,
  SaasPlanService,
} from '../../../../../core/services/saas-plan.service';

@Component({
  selector: 'app-configuracion-home',
  standalone: true,
  imports: [CommonModule, Sidebar, Topbar],
  templateUrl: './configuracion-home.html',
  styleUrl: './configuracion-home.css',
})
export class ConfiguracionHome implements OnInit {
  private usuariosService = inject(UsuariosService);
  private saasPlanService = inject(SaasPlanService);

  usuarios: Usuario[] = [];
  cargandoUsuarios = false;

  bloqueoVisible = false;
  bloqueoTitulo = '';
  bloqueoMensaje = '';
  bloqueoPlanActual = '';
  bloqueoPlanRequerido = '';

  ngOnInit(): void {
    this.cargarUsuarios();
  }

  cargarUsuarios(): void {
    this.cargandoUsuarios = true;

    this.usuariosService.listar().subscribe({
      next: (usuarios) => {
        this.usuarios = usuarios?.length ? usuarios : this.obtenerUsuariosDemo();
        this.cargandoUsuarios = false;
      },
      error: () => {
        // Mientras el backend no esté activo, usamos datos internos sin mostrar aviso visual.
        this.usuarios = this.obtenerUsuariosDemo();
        this.cargandoUsuarios = false;
      },
    });
  }

  puedeUsar(feature: SaasFeatureKey): boolean {
    return this.saasPlanService.canAccess(feature);
  }

  accionBloqueada(feature: SaasFeatureKey): boolean {
    return !this.puedeUsar(feature);
  }

  ejecutarAccion(feature: SaasFeatureKey, titulo: string): void {
    if (this.puedeUsar(feature)) {
      return;
    }

    const planActual = this.saasPlanService.getActivePlan();
    const planRequerido = this.saasPlanService.getRequiredPlan(feature);

    this.bloqueoTitulo = titulo;
    this.bloqueoMensaje = this.saasPlanService.getLockMessage(feature);
    this.bloqueoPlanActual = this.saasPlanService.getPlanLabel(planActual);
    this.bloqueoPlanRequerido = this.saasPlanService.getPlanLabel(planRequerido);
    this.bloqueoVisible = true;
  }

  cerrarBloqueo(): void {
    this.bloqueoVisible = false;
    this.bloqueoTitulo = '';
    this.bloqueoMensaje = '';
    this.bloqueoPlanActual = '';
    this.bloqueoPlanRequerido = '';
  }

  getPlanActual(): string {
    return this.saasPlanService.getPlanLabel(this.saasPlanService.getActivePlan());
  }

  getPlanActualClass(): string {
    const plan = this.saasPlanService.getActivePlan();

    if (plan === 'premium') return 'plan-premium';
    if (plan === 'profesional') return 'plan-profesional';
    if (plan === 'esencial') return 'plan-esencial';

    return 'plan-sin-plan';
  }

  private obtenerUsuariosDemo(): Usuario[] {
    return [
      {
        id: 1,
        username: 'admin',
        firstName: 'Administrador',
        surnames: 'General',
        email: 'admin@empresa.com',
        estado: true,
        roleNames: ['ADMIN'],
      },
      {
        id: 2,
        username: 'rrhh',
        firstName: 'Analista',
        surnames: 'RRHH',
        email: 'rrhh@empresa.com',
        estado: true,
        roleNames: ['RRHH'],
      },
      {
        id: 3,
        username: 'supervisor',
        firstName: 'Supervisor',
        surnames: 'Área',
        email: 'supervisor@empresa.com',
        estado: true,
        roleNames: ['SUPERVISOR'],
      },
    ];
  }
}