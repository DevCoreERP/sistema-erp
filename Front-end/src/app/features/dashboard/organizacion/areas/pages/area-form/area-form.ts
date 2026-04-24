import { Component } from '@angular/core';
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

  area = {
    nombre: '',
    descripcion: '',
    responsable: '',
    objetivo: '',
    observaciones: '',
    estado: 'Activa' as 'Activa' | 'Inactiva',
  };

  constructor(
    private areasService: AreasService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.route.paramMap.subscribe((params) => {
      const idParam = params.get('id');

      if (idParam) {
        this.modoEdicion = true;
        this.areaId = Number(idParam);

        const areaExistente = this.areasService.obtenerAreaPorId(this.areaId);

        if (areaExistente) {
          this.area = {
            nombre: areaExistente.nombre,
            descripcion: areaExistente.descripcion || '',
            responsable: '',
            objetivo: '',
            observaciones: '',
            estado: areaExistente.estado,
          };
        }
      }
    });
  }

  guardarArea(): void {
    this.enviado = true;

    if (!this.area.nombre || !this.area.descripcion || !this.area.responsable) {
      return;
    }

    if (this.modoEdicion) {
      this.areasService.actualizarArea(this.areaId, {
        nombre: this.area.nombre,
        descripcion: this.area.descripcion,
        estado: this.area.estado,
      });

      alert('Área actualizada correctamente');
    } else {
      this.areasService.agregarArea({
        nombre: this.area.nombre,
        descripcion: this.area.descripcion,
        estado: this.area.estado,
      });

      alert('Área registrada correctamente');
    }

    this.router.navigate(['/areas']);
  }
}