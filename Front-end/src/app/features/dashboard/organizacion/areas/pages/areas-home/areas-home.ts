import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Sidebar } from '../../../../components/sidebar/sidebar';
import { Topbar } from '../../../../components/topbar/topbar';
import { AreasService } from '../../service/areas.service';
import { Area } from '../../interface/area.interface';

@Component({
  selector: 'app-areas-home',
  standalone: true,
  imports: [CommonModule, FormsModule, Sidebar, Topbar],
  templateUrl: './areas-home.html',
  styleUrl: './areas-home.css',
})
export class AreasHome implements OnInit {
  terminoBusqueda = '';
  areas: Area[] = [];

  nuevaArea = { nombre: '' };
  creadoArea = false;

  constructor(
    private areasService: AreasService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.cargarAreas();
  }

  cargarAreas(): void {
    this.areasService.obtenerAreas().subscribe({
      next: (data) => {
        this.areas = data;
        this.cdr.detectChanges();
      },
      error: () => {
        alert('Error al cargar las áreas');
      },
    });
  }

  get areasFiltradas(): Area[] {
    const termino = this.terminoBusqueda.toLowerCase().trim();

    if (!termino) {
      return this.areas;
    }

    return this.areas.filter((area) =>
      area.nombre.toLowerCase().includes(termino)
    );
  }

  get totalActivas(): number {
    return this.areas.filter((area) => area.active).length;
  }

  get totalInactivas(): number {
    return this.areas.filter((area) => !area.active).length;
  }

  editarArea(id: number): void {
    this.router.navigate(['/areas/editar', id]);
  }

  eliminarArea(id: number): void {
    if (confirm('¿Está seguro de eliminar esta área?')) {
      this.areasService.eliminarArea(id).subscribe({
        next: () => {
          this.cargarAreas();
        },
        error: () => {
          alert('Error al eliminar el área');
        },
      });
    }
  }

  crearAreaInline(): void {
    this.creadoArea = true;
    if (!this.nuevaArea.nombre) return;

    this.areasService.agregarArea({ nombre: this.nuevaArea.nombre }).subscribe({
      next: () => {
        this.nuevaArea.nombre = '';
        this.creadoArea = false;
        this.cargarAreas();
      },
      error: () => {
        alert('Error al registrar el área');
      },
    });
  }

  formatearFecha(fecha: string): string {
    if (!fecha) return '—';
    const date = new Date(fecha);
    return date.toLocaleDateString('es-ES');
  }
}
