import { ChangeDetectorRef, Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { Sidebar } from '../../../../components/sidebar/sidebar';
import { Topbar } from '../../../../components/topbar/topbar';
import { DepartamentosService } from '../../service/departamentos.service';
import { AreasService } from '../../../areas/service/areas.service';
import { Area } from '../../../areas/interface/area.interface';

@Component({
  selector: 'app-departamento-form',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, Sidebar, Topbar],
  templateUrl: './departamento-form.html',
  styleUrl: './departamento-form.css',
})
export class DepartamentoForm {
  modoEdicion = false;
  departamentoId!: number;
  enviado = false;
  cargando = false;

  departamento = {
    nombre: '',
    areaId: 0,
  };

  areas: Area[] = [];

  constructor(
    private departamentosService: DepartamentosService,
    private areasService: AreasService,
    private router: Router,
    private route: ActivatedRoute,
    private cdr: ChangeDetectorRef
  ) {
    this.areasService.obtenerAreas().subscribe({
      next: (data) => {
        this.areas = data;
        this.cdr.detectChanges();
      },
      error: () => {},
    });

    this.route.paramMap.subscribe((params) => {
      const idParam = params.get('id');
      if (idParam) {
        this.modoEdicion = true;
        this.departamentoId = Number(idParam);
        this.departamentosService.obtenerDepartamentoPorId(this.departamentoId).subscribe({
          next: (dept) => {
            this.departamento = {
              nombre: dept.nombre,
              areaId: dept.area?.id || 0,
            };
            this.cdr.detectChanges();
          },
          error: () => {
            alert('No se pudo cargar el departamento');
            this.router.navigate(['/departamentos']);
          },
        });
      }
    });
  }

  guardarDepartamento(): void {
    this.enviado = true;
    if (!this.departamento.nombre || !this.departamento.areaId) return;

    this.cargando = true;

    if (this.modoEdicion) {
      this.departamentosService.actualizarDepartamento(this.departamentoId, {
        nombre: this.departamento.nombre,
        areaId: this.departamento.areaId,
      }).subscribe({
        next: () => {
          alert('Departamento actualizado correctamente');
          this.router.navigate(['/departamentos']);
        },
        error: () => {
          alert('Error al actualizar el departamento');
          this.cargando = false;
        },
      });
    } else {
      this.departamentosService.agregarDepartamento({
        nombre: this.departamento.nombre,
        areaId: this.departamento.areaId,
      }).subscribe({
        next: () => {
          alert('Departamento registrado correctamente');
          this.router.navigate(['/departamentos']);
        },
        error: () => {
          alert('Error al registrar el departamento');
          this.cargando = false;
        },
      });
    }
  }
}
