import { Component, inject } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './sidebar.html',
  styleUrls: ['./sidebar.css'],
})
export class Sidebar {
  private router = inject(Router);

  orgExpanded =
    this.router.url.includes('/areas') ||
    this.router.url.includes('/departamentos') ||
    this.router.url.includes('/cargos');

  turnosExpanded =
    this.router.url.includes('/gestion-turnos') ||
    this.router.url.includes('/asignacion-turnos');

  toggleOrg(): void {
    this.orgExpanded = !this.orgExpanded;
  }

  toggleTurnos(): void {
    this.turnosExpanded = !this.turnosExpanded;
  }
}