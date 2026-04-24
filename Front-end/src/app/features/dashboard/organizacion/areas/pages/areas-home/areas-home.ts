import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { Sidebar } from '../../../../components/sidebar/sidebar';
import { Topbar } from '../../../../components/topbar/topbar';
import { AreasService } from '../../service/areas.service';
import { Area } from '../../interface/area.interface';

@Component({
  selector: 'app-areas-home',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, Sidebar, Topbar],
  templateUrl: './areas-home.html',
  styleUrl: './areas-home.css',
})
export class AreasHome {
  terminoBusqueda = '';
  areas: Area[] = [];

  constructor(
    private areasService: AreasService,
    private router: Router
  ) {
    this.cargarAreas();
  }

  cargarAreas(): void {
    this.areas = this.areasService.obtenerAreas();
  }

  get areasFiltradas(): Area[] {
    const termino = this.terminoBusqueda.toLowerCase().trim();

    if (!termino) {
      return this.areas;
    }

    return this.areas.filter((area) =>
      area.nombre.toLowerCase().includes(termino) ||
      (area.descripcion || '').toLowerCase().includes(termino)
    );
  }

  get totalActivas(): number {
    return this.areas.filter((area) => area.estado === 'Activa').length;
  }

  get totalInactivas(): number {
    return this.areas.filter((area) => area.estado === 'Inactiva').length;
  }

  irNuevaArea(): void {
    this.router.navigate(['/areas/nueva']);
  }

  verArea(id: number): void {
    this.router.navigate(['/areas/ver', id]);
  }

  editarArea(id: number): void {
    this.router.navigate(['/areas/editar', id]);
  }

  toggleEstado(area: Area): void {
    this.areasService.cambiarEstado(area.id);
    this.cargarAreas();
  }

  obtenerResponsable(id: number): string {
    const responsables = [
      'Jefatura administrativa',
      'Gerencia financiera',
      'Coordinación TI',
      'Dirección general',
      'Unidad operativa',
    ];

    return responsables[(id - 1) % responsables.length];
  }

  obtenerFechaReferencia(id: number): string {
    const fechas = [
      '22/04/2026',
      '20/04/2026',
      '18/04/2026',
      '15/04/2026',
      '12/04/2026',
    ];

    return fechas[(id - 1) % fechas.length];
  }
}