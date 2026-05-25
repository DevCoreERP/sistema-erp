import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Sidebar } from '../../../../components/sidebar/sidebar';
import { Topbar } from '../../../../components/topbar/topbar';
import { CargosService } from '../../service/cargos.service';
import { DepartamentosService } from '../../../departamentos/service/departamentos.service';
import { Cargo } from '../../interface/cargo.interface';
import { Departamento } from '../../../departamentos/interface/departamento.interface';

@Component({
  selector: 'app-cargos-home',
  standalone: true,
  imports: [CommonModule, FormsModule, Sidebar, Topbar],
  templateUrl: './cargos-home.html',
  styleUrl: './cargos-home.css',
})
export class CargosHome implements OnInit {
  terminoBusqueda = '';
  cargos: Cargo[] = [];
  departamentos: Departamento[] = [];

  nuevoCargo = { nombre: '', departamentoId: 0 };
  creadoCargo = false;

  constructor(
    private cargosService: CargosService,
    private departamentosService: DepartamentosService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.cargarCargos();
    this.cargarDepartamentos();
  }

  cargarCargos(): void {
    this.cargosService.obtenerCargos().subscribe({
      next: (data) => {
        this.cargos = data;
        this.cdr.detectChanges();
      },
      error: () => {
        alert('Error al cargar los cargos');
      },
    });
  }

  get cargosFiltrados(): Cargo[] {
    const termino = this.terminoBusqueda.toLowerCase().trim();
    if (!termino) return this.cargos;
    return this.cargos.filter((c) =>
      c.nombre.toLowerCase().includes(termino)
    );
  }

  get totalActivos(): number {
    return this.cargos.filter((c) => c.active).length;
  }

  get totalInactivos(): number {
    return this.cargos.filter((c) => !c.active).length;
  }

  crearCargoInline(): void {
    this.creadoCargo = true;
    if (!this.nuevoCargo.nombre || !this.nuevoCargo.departamentoId) return;

    this.cargosService
      .agregarCargo({
        nombre: this.nuevoCargo.nombre,
        departamentoId: this.nuevoCargo.departamentoId,
      })
      .subscribe({
        next: () => {
          this.nuevoCargo = { nombre: '', departamentoId: 0 };
          this.creadoCargo = false;
          this.cargarCargos();
        },
        error: () => {
          alert('Error al registrar el cargo');
        },
      });
  }

  cargarDepartamentos(): void {
    this.departamentosService.obtenerDepartamentos().subscribe({
      next: (data) => {
        this.departamentos = data;
        this.cdr.detectChanges();
      },
      error: () => {},
    });
  }

  irNuevoCargo(): void {
    this.router.navigate(['/cargos/nuevo']);
  }

  editarCargo(id: number): void {
    this.router.navigate(['/cargos/editar', id]);
  }

  eliminarCargo(id: number): void {
    if (confirm('¿Está seguro de eliminar este cargo?')) {
      this.cargosService.eliminarCargo(id).subscribe({
        next: () => {
          this.cargarCargos();
        },
        error: () => {
          alert('Error al eliminar el cargo');
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
