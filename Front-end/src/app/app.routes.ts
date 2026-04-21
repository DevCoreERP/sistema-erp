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
];
