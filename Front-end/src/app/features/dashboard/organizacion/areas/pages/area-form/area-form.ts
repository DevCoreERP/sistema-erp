import { ChangeDetectorRef, Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { Sidebar } from '../../../../components/sidebar/sidebar';
import { Topbar } from '../../../../components/topbar/topbar';
import { AreasService } from '../../service/areas.service';

@Component({
  selector: 'app-area-form',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, Sidebar, Topbar],
  templateUrl: './area-form.html',
  styleUrl: './area-form.css',
})
export class AreaForm {
  modoEdicion = false;
  areaId!: number;
  enviado = false;
  cargando = false;

  area = {
    nombre: '',
  };

  constructor(
    private areasService: AreasService,
    private router: Router,
    private route: ActivatedRoute,
    private cdr: ChangeDetectorRef
  ) {
    this.route.paramMap.subscribe((params) => {
      const idParam = params.get('id');

      if (idParam) {
        this.modoEdicion = true;
        this.areaId = Number(idParam);

        this.areasService.obtenerAreaPorId(this.areaId).subscribe({
          next: (areaExistente) => {
            this.area = {
              nombre: areaExistente.nombre,
            };
            this.cdr.detectChanges();
          },
          error: () => {
            alert('No se pudo cargar el área');
            this.router.navigate(['/areas']);
          },
        });
      }
    });
  }

  guardarArea(): void {
    this.enviado = true;

    if (!this.area.nombre) {
      return;
    }

    this.cargando = true;

    if (this.modoEdicion) {
      this.areasService.actualizarArea(this.areaId, {
        nombre: this.area.nombre,
      }).subscribe({
        next: () => {
          alert('Área actualizada correctamente');
          this.router.navigate(['/areas']);
        },
        error: () => {
          alert('Error al actualizar el área');
          this.cargando = false;
        },
      });
    } else {
      this.areasService.agregarArea({
        nombre: this.area.nombre,
      }).subscribe({
        next: () => {
          alert('Área registrada correctamente');
          this.router.navigate(['/areas']);
        },
        error: () => {
          alert('Error al registrar el área');
          this.cargando = false;
        },
      });
    }
  }
}
