import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Sidebar } from '../../../../components/sidebar/sidebar';
import { Topbar } from '../../../../components/topbar/topbar';
import { AreasService } from '../../service/areas.service';
import { Area } from '../../interface/area.interface';

@Component({
  selector: 'app-area-detalle',
  standalone: true,
  imports: [CommonModule, RouterLink, Sidebar, Topbar],
  templateUrl: './area-detalle.html',
  styleUrl: './area-detalle.css',
})
export class AreaDetalle {
  area?: Area;

  constructor(
    private route: ActivatedRoute,
    private areasService: AreasService
  ) {
    this.route.paramMap.subscribe((params) => {
      const id = Number(params.get('id'));
      this.areasService.obtenerAreaPorId(id).subscribe({
        next: (data) => {
          this.area = data;
        },
        error: () => {
          alert('No se pudo cargar el área');
        },
      });
    });
  }

  formatearFecha(fecha: string): string {
    if (!fecha) return '—';
    const date = new Date(fecha);
    return date.toLocaleDateString('es-ES');
  }
}
