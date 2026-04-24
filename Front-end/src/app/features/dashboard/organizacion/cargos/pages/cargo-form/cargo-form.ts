import { ChangeDetectorRef, Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { Sidebar } from '../../../../components/sidebar/sidebar';
import { Topbar } from '../../../../components/topbar/topbar';
import { CargosService } from '../../service/cargos.service';
import { DepartamentosService } from '../../../departamentos/service/departamentos.service';
import { Departamento } from '../../../departamentos/interface/departamento.interface';

@Component({
  selector: 'app-cargo-form',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, Sidebar, Topbar],
  templateUrl: './cargo-form.html',
  styleUrl: './cargo-form.css',
})
export class CargoForm {
  modoEdicion = false;
  cargoId!: number;
  enviado = false;
  cargando = false;

  cargo = {
    nombre: '',
    departamentoId: 0,
  };

  departamentos: Departamento[] = [];

  constructor(
    private cargosService: CargosService,
    private departamentosService: DepartamentosService,
    private router: Router,
    private route: ActivatedRoute,
    private cdr: ChangeDetectorRef
  ) {
    this.departamentosService.obtenerDepartamentos().subscribe({
      next: (data) => {
        this.departamentos = data;
        this.cdr.detectChanges();
      },
      error: () => {},
    });

    this.route.paramMap.subscribe((params) => {
      const idParam = params.get('id');
      if (idParam) {
        this.modoEdicion = true;
        this.cargoId = Number(idParam);
        this.cargosService.obtenerCargoPorId(this.cargoId).subscribe({
          next: (c) => {
            this.cargo = {
              nombre: c.nombre,
              departamentoId: c.departamento?.id || 0,
            };
            this.cdr.detectChanges();
          },
          error: () => {
            alert('No se pudo cargar el cargo');
            this.router.navigate(['/cargos']);
          },
        });
      }
    });
  }

  guardarCargo(): void {
    this.enviado = true;
    if (!this.cargo.nombre || !this.cargo.departamentoId) return;

    this.cargando = true;

    if (this.modoEdicion) {
      this.cargosService.actualizarCargo(this.cargoId, {
        nombre: this.cargo.nombre,
        departamentoId: this.cargo.departamentoId,
      }).subscribe({
        next: () => {
          alert('Cargo actualizado correctamente');
          this.router.navigate(['/cargos']);
        },
        error: () => {
          alert('Error al actualizar el cargo');
          this.cargando = false;
        },
      });
    } else {
      this.cargosService.agregarCargo({
        nombre: this.cargo.nombre,
        departamentoId: this.cargo.departamentoId,
      }).subscribe({
        next: () => {
          alert('Cargo registrado correctamente');
          this.router.navigate(['/cargos']);
        },
        error: () => {
          alert('Error al registrar el cargo');
          this.cargando = false;
        },
      });
    }
  }
}
