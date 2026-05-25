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
  {
    path: 'panel',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/dashboard/pages/dashboard-home/dashboard-home').then(
        (m) => m.DashboardHome
      ),
  },
  {
    path: 'empleados',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/dashboard/empleados/pages/empleados-home/empleados-home').then(
        (m) => m.EmpleadosHome
      ),
  },
  {
    path: 'empleados/nuevo',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/dashboard/empleados/pages/empleado-form/empleado-form').then(
        (m) => m.EmpleadoForm
      ),
  },
  {
    path: 'empleados/ver/:id',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/dashboard/empleados/pages/empleado-detalle/empleado-detalle').then(
        (m) => m.EmpleadoDetalle
      ),
  },
  {
    path: 'empleados/editar/:id',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/dashboard/empleados/pages/empleado-form/empleado-form').then(
        (m) => m.EmpleadoForm
      ),
  },
  {
    path: 'asistencia',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/dashboard/pages/dashboard-home/dashboard-home').then(
        (m) => m.DashboardHome
      ),
  },
  {
    path: 'nomina',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/dashboard/pages/dashboard-home/dashboard-home').then(
        (m) => m.DashboardHome
      ),
  },
  {
    path: 'permisos',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/dashboard/pages/dashboard-home/dashboard-home').then(
        (m) => m.DashboardHome
      ),
  },
  {
    path: 'vacaciones',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/dashboard/pages/dashboard-home/dashboard-home').then(
        (m) => m.DashboardHome
      ),
  },
  {
    path: 'reportes',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/dashboard/pages/dashboard-home/dashboard-home').then(
        (m) => m.DashboardHome
      ),
  },
  {
    path: 'perfil',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/dashboard/perfil/page/perfil').then(
        (m) => m.PerfilPage
      ),
  },
  {
    path: 'configuracion',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/dashboard/pages/dashboard-home/dashboard-home').then(
        (m) => m.DashboardHome
      ),
  },
  {
  path: 'areas',
  canActivate: [authGuard],
  loadComponent: () =>
    import('./features/dashboard/organizacion/areas/pages/areas-home/areas-home').then(
      (m) => m.AreasHome
    ),
},
{
  path: 'areas/nueva',
  canActivate: [authGuard],
  loadComponent: () =>
    import('./features/dashboard/organizacion/areas/pages/area-form/area-form').then(
      (m) => m.AreaForm
    ),
},
{
  path: 'areas/editar/:id',
  canActivate: [authGuard],
  loadComponent: () =>
    import('./features/dashboard/organizacion/areas/pages/area-form/area-form').then(
      (m) => m.AreaForm
    ),
},
{
  path: 'departamentos',
  canActivate: [authGuard],
  loadComponent: () =>
    import('./features/dashboard/organizacion/departamentos/pages/departamentos-home/departamentos-home').then(
      (m) => m.DepartamentosHome
    ),
},
{
  path: 'departamentos/nuevo',
  canActivate: [authGuard],
  loadComponent: () =>
    import('./features/dashboard/organizacion/departamentos/pages/departamento-form/departamento-form').then(
      (m) => m.DepartamentoForm
    ),
},
{
  path: 'departamentos/editar/:id',
  canActivate: [authGuard],
  loadComponent: () =>
    import('./features/dashboard/organizacion/departamentos/pages/departamento-form/departamento-form').then(
      (m) => m.DepartamentoForm
    ),
},
{
  path: 'cargos',
  canActivate: [authGuard],
  loadComponent: () =>
    import('./features/dashboard/organizacion/cargos/pages/cargos-home/cargos-home').then(
      (m) => m.CargosHome
    ),
},
{
  path: 'cargos/nuevo',
  canActivate: [authGuard],
  loadComponent: () =>
    import('./features/dashboard/organizacion/cargos/pages/cargo-form/cargo-form').then(
      (m) => m.CargoForm
    ),
},
{
  path: 'cargos/editar/:id',
  canActivate: [authGuard],
  loadComponent: () =>
    import('./features/dashboard/organizacion/cargos/pages/cargo-form/cargo-form').then(
      (m) => m.CargoForm
    ),
},
];
