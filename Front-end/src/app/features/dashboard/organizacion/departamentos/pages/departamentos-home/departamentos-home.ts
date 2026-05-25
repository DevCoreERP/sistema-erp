import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Sidebar } from '../../../../components/sidebar/sidebar';
import { Topbar } from '../../../../components/topbar/topbar';
import { DepartamentosService } from '../../service/departamentos.service';
import { AreasService } from '../../../areas/service/areas.service';
import { Departamento } from '../../interface/departamento.interface';
import { Area } from '../../../areas/interface/area.interface';

@Component({
  selector: 'app-departamentos-home',
  standalone: true,
  imports: [CommonModule, FormsModule, Sidebar, Topbar],
  templateUrl: './departamentos-home.html',
  styleUrl: './departamentos-home.css',
})
export class DepartamentosHome implements OnInit {
  terminoBusqueda = '';
  departamentos: Departamento[] = [];
  areas: Area[] = [];

  nuevoDepartamento = { nombre: '', areaId: 0 };
  creadoDept = false;

  constructor(
    private departamentosService: DepartamentosService,
    private areasService: AreasService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.cargarDepartamentos();
    this.cargarAreas();
  }

  cargarDepartamentos(): void {
    this.departamentosService.obtenerDepartamentos().subscribe({
      next: (data) => {
        this.departamentos = data;
        this.cdr.detectChanges();
      },
      error: () => {
        alert('Error al cargar los departamentos');
      },
    });
  }

  get departamentosFiltrados(): Departamento[] {
    const termino = this.terminoBusqueda.toLowerCase().trim();
    if (!termino) return this.departamentos;
    return this.departamentos.filter((d) =>
      d.nombre.toLowerCase().includes(termino)
    );
  }

  get totalActivos(): number {
    return this.departamentos.filter((d) => d.active).length;
  }

  get totalInactivos(): number {
    return this.departamentos.filter((d) => !d.active).length;
  }

  crearDepartamentoInline(): void {
    this.creadoDept = true;
    if (!this.nuevoDepartamento.nombre || !this.nuevoDepartamento.areaId) return;

    this.departamentosService
      .agregarDepartamento({
        nombre: this.nuevoDepartamento.nombre,
        areaId: this.nuevoDepartamento.areaId,
      })
      .subscribe({
        next: () => {
          this.nuevoDepartamento = { nombre: '', areaId: 0 };
          this.creadoDept = false;
          this.cargarDepartamentos();
        },
        error: () => {
          alert('Error al registrar el departamento');
        },
      });
  }

  cargarAreas(): void {
    this.areasService.obtenerAreas().subscribe({
      next: (data) => {
        this.areas = data;
        this.cdr.detectChanges();
      },
      error: () => {},
    });
  }

  irNuevoDepartamento(): void {
    this.router.navigate(['/departamentos/nuevo']);
  }

  editarDepartamento(id: number): void {
    this.router.navigate(['/departamentos/editar', id]);
  }

  eliminarDepartamento(id: number): void {
    if (confirm('¿Está seguro de eliminar este departamento?')) {
      this.departamentosService.eliminarDepartamento(id).subscribe({
        next: () => {
          this.cargarDepartamentos();
        },
        error: () => {
          alert('Error al eliminar el departamento');
        },
      });
    }
  }

  formatearFecha(fecha: string): string {
    if (!fecha) return '—';
    const date = new Date(fecha);
    return date.toLocaleDateString('es-ES');
  }
}
