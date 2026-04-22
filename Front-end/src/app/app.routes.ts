import { Routes } from '@angular/router';

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
  loadComponent: () =>
    import('./features/dashboard/pages/dashboard-home/dashboard-home').then(
      (m) => m.DashboardHome
    ),
},
  {
    path: 'empleados',
    loadComponent: () =>
      import('./features/dashboard/pages/dashboard-home/dashboard-home').then(
        (m) => m.DashboardHome
      ),
  },
  {
    path: 'asistencia',
    loadComponent: () =>
      import('./features/dashboard/pages/dashboard-home/dashboard-home').then(
        (m) => m.DashboardHome
      ),
  },
  {
    path: 'nomina',
    loadComponent: () =>
      import('./features/dashboard/pages/dashboard-home/dashboard-home').then(
        (m) => m.DashboardHome
      ),
  },
  {
    path: 'permisos',
    loadComponent: () =>
      import('./features/dashboard/pages/dashboard-home/dashboard-home').then(
        (m) => m.DashboardHome
      ),
  },
  {
    path: 'vacaciones',
    loadComponent: () =>
      import('./features/dashboard/pages/dashboard-home/dashboard-home').then(
        (m) => m.DashboardHome
      ),
  },
  {
    path: 'reportes',
    loadComponent: () =>
      import('./features/dashboard/pages/dashboard-home/dashboard-home').then(
        (m) => m.DashboardHome
      ),
  },
  {
    path: 'configuracion',
    loadComponent: () =>
      import('./features/dashboard/pages/dashboard-home/dashboard-home').then(
        (m) => m.DashboardHome
      ),
  },
];