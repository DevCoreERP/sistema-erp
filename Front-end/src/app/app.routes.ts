import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'inicio',
  },
  {
    path: 'inicio',
    loadComponent: () =>
      import('./features/landing/page/landing').then((m) => m.Landing),
  },
  {
    path: 'iniciar-sesion',
    loadComponent: () =>
      import('./features/auth/login/login').then((m) => m.Login),
  },
  {
    path: 'prueba-gratuita',
    loadComponent: () =>
      import('./features/auth/trial/trial').then((m) => m.Trial),
  },
  {
    path: 'servicios',
    loadComponent: () =>
      import('./features/landing/page/landing').then((m) => m.Landing),
  },
  {
    path: 'nosotros',
    loadComponent: () =>
      import('./features/landing/page/landing').then((m) => m.Landing),
  },
  {
    path: 'contacto',
    loadComponent: () =>
      import('./features/landing/page/landing').then((m) => m.Landing),
  },

  // DASHBOARD PRINCIPAL
  {
    path: 'panel',
    canActivate: [authGuard],
    data: { layout: 'dashboard' },
    loadComponent: () =>
      import('./features/dashboard/pages/dashboard-home/dashboard-home').then(
        (m) => m.DashboardHome
      ),
  },

  // EMPLEADOS
  {
    path: 'empleados',
    canActivate: [authGuard],
    data: { layout: 'dashboard' },
    loadComponent: () =>
      import('./features/dashboard/empleados/pages/empleados-home/empleados-home').then(
        (m) => m.EmpleadosHome
      ),
  },
  {
    path: 'empleados/nuevo',
    canActivate: [authGuard],
    data: { layout: 'dashboard' },
    loadComponent: () =>
      import('./features/dashboard/empleados/pages/empleado-form/empleado-form').then(
        (m) => m.EmpleadoForm
      ),
  },
  {
    path: 'empleados/ver/:id',
    canActivate: [authGuard],
    data: { layout: 'dashboard' },
    loadComponent: () =>
      import('./features/dashboard/empleados/pages/empleado-detalle/empleado-detalle').then(
        (m) => m.EmpleadoDetalle
      ),
  },
  {
    path: 'empleados/editar/:id',
    canActivate: [authGuard],
    data: { layout: 'dashboard' },
    loadComponent: () =>
      import('./features/dashboard/empleados/pages/empleado-form/empleado-form').then(
        (m) => m.EmpleadoForm
      ),
  },

  // GESTIÓN ORGANIZACIONAL - ÁREAS
  {
    path: 'areas',
    canActivate: [authGuard],
    data: { layout: 'dashboard' },
    loadComponent: () =>
      import('./features/dashboard/organizacion/areas/pages/areas-home/areas-home').then(
        (m) => m.AreasHome
      ),
  },
  {
    path: 'areas/nueva',
    canActivate: [authGuard],
    data: { layout: 'dashboard' },
    loadComponent: () =>
      import('./features/dashboard/organizacion/areas/pages/area-form/area-form').then(
        (m) => m.AreaForm
      ),
  },
  {
    path: 'areas/editar/:id',
    canActivate: [authGuard],
    data: { layout: 'dashboard' },
    loadComponent: () =>
      import('./features/dashboard/organizacion/areas/pages/area-form/area-form').then(
        (m) => m.AreaForm
      ),
  },

  // GESTIÓN ORGANIZACIONAL - DEPARTAMENTOS
  {
    path: 'departamentos',
    canActivate: [authGuard],
    data: { layout: 'dashboard' },
    loadComponent: () =>
      import('./features/dashboard/organizacion/departamentos/pages/departamentos-home/departamentos-home').then(
        (m) => m.DepartamentosHome
      ),
  },
  {
    path: 'departamentos/nuevo',
    canActivate: [authGuard],
    data: { layout: 'dashboard' },
    loadComponent: () =>
      import('./features/dashboard/organizacion/departamentos/pages/departamento-form/departamento-form').then(
        (m) => m.DepartamentoForm
      ),
  },
  {
    path: 'departamentos/editar/:id',
    canActivate: [authGuard],
    data: { layout: 'dashboard' },
    loadComponent: () =>
      import('./features/dashboard/organizacion/departamentos/pages/departamento-form/departamento-form').then(
        (m) => m.DepartamentoForm
      ),
  },

  // GESTIÓN ORGANIZACIONAL - CARGOS
  {
    path: 'cargos',
    canActivate: [authGuard],
    data: { layout: 'dashboard' },
    loadComponent: () =>
      import('./features/dashboard/organizacion/cargos/pages/cargos-home/cargos-home').then(
        (m) => m.CargosHome
      ),
  },
  {
    path: 'cargos/nuevo',
    canActivate: [authGuard],
    data: { layout: 'dashboard' },
    loadComponent: () =>
      import('./features/dashboard/organizacion/cargos/pages/cargo-form/cargo-form').then(
        (m) => m.CargoForm
      ),
  },
  {
    path: 'cargos/editar/:id',
    canActivate: [authGuard],
    data: { layout: 'dashboard' },
    loadComponent: () =>
      import('./features/dashboard/organizacion/cargos/pages/cargo-form/cargo-form').then(
        (m) => m.CargoForm
      ),
  },

  // ASISTENCIA
  {
    path: 'asistencia',
    canActivate: [authGuard],
    data: { layout: 'dashboard' },
    loadComponent: () =>
      import('./features/dashboard/asistencia/pages/asistencia-home/asistencia-home').then(
        (m) => m.AsistenciaHome
      ),
  },

  // TURNOS LABORALES
  {
    path: 'gestion-turnos',
    canActivate: [authGuard],
    data: { layout: 'dashboard' },
    loadComponent: () =>
      import('./features/dashboard/turnos/pages/gestion-turnos/gestion-turnos').then(
        (m) => m.GestionTurnosComponent
      ),
  },
  {
    path: 'asignacion-turnos',
    canActivate: [authGuard],
    data: { layout: 'dashboard' },
    loadComponent: () =>
      import('./features/dashboard/turnos/pages/asignacion-turnos/asignacion-turnos').then(
        (m) => m.AsignacionTurnos
      ),
  },

  // NÓMINAS
  {
    path: 'nominas',
    canActivate: [authGuard],
    data: { layout: 'dashboard' },
    loadComponent: () =>
      import('./features/dashboard/nominas/pages/nominas-home/nominas-home').then(
        (m) => m.NominasHome
      ),
  },
  {
    path: 'nomina',
    canActivate: [authGuard],
    data: { layout: 'dashboard' },
    loadComponent: () =>
      import('./features/dashboard/nominas/pages/nominas-home/nominas-home').then(
        (m) => m.NominasHome
      ),
  },

  // PERMISOS LABORALES
  {
    path: 'permisos',
    canActivate: [authGuard],
    data: { layout: 'dashboard' },
    loadComponent: () =>
      import('./features/dashboard/permisos/pages/permisos-home/permisos-home').then(
        (m) => m.PermisosHome
      ),
  },

  // VACACIONES
  {
    path: 'vacaciones',
    canActivate: [authGuard],
    data: { layout: 'dashboard' },
    loadComponent: () =>
      import('./features/dashboard/vacaciones/pages/vacaciones-home/vacaciones-home').then(
        (m) => m.VacacionesHome
      ),
  },

  // SAAS - MI SUSCRIPCIÓN
  {
    path: 'saas',
    canActivate: [authGuard],
    data: { layout: 'dashboard' },
    loadComponent: () =>
      import('./features/dashboard/saas/pages/saas-home/saas-home').then(
        (m) => m.SaasHome
      ),
  },
  {
    path: 'mi-suscripcion',
    redirectTo: 'saas',
    pathMatch: 'full',
  },
  {
    path: 'suscripcion',
    redirectTo: 'saas',
    pathMatch: 'full',
  },

  // SAAS - SELECCIÓN DE PLANES
  {
    path: 'saas/planes',
    canActivate: [authGuard],
    data: { layout: 'dashboard' },
    loadComponent: () =>
      import('./features/dashboard/saas/pages/saas-plans/saas-plans').then(
        (m) => m.SaasPlans
      ),
  },

  // SAAS - FACTURACIÓN
  {
    path: 'saas/facturacion',
    canActivate: [authGuard],
    data: { layout: 'dashboard' },
    loadComponent: () =>
      import('./features/dashboard/saas/pages/saas-billing/saas-billing').then(
        (m) => m.SaasBilling
      ),
  },

  // REPORTES
  {
    path: 'reportes',
    canActivate: [authGuard],
    data: { layout: 'dashboard' },
    loadComponent: () =>
      import('./features/dashboard/reportes/pages/reportes-home/reportes-home').then(
        (m) => m.ReportesHomeComponent
      ),
  },

  // PERFIL
  {
    path: 'perfil',
    canActivate: [authGuard],
    data: { layout: 'dashboard' },
    loadComponent: () =>
      import('./features/dashboard/perfil/page/perfil').then(
        (m) => m.PerfilPage
      ),
  },

  // CONFIGURACIÓN
  {
    path: 'configuracion',
    canActivate: [authGuard],
    data: { layout: 'dashboard' },
    loadComponent: () =>
      import('./features/dashboard/configuracion/pages/configuracion-home/configuracion-home').then(
        (m) => m.ConfiguracionHome
      ),
  },

  // SISTEMA
  {
    path: 'sistema/bitacora',
    canActivate: [authGuard],
    data: { layout: 'dashboard' },
    loadComponent: () =>
      import('./features/dashboard/sistema/bitacora/bitacora').then(
        (m) => m.Bitacora
      ),
  },

  // RUTA NO ENCONTRADA
  {
    path: '**',
    redirectTo: 'inicio',
  },
];