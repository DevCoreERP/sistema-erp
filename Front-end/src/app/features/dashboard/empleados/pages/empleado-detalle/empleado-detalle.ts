import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Sidebar } from '../../../components/sidebar/sidebar';
import { Topbar } from '../../../components/topbar/topbar';
import { EmpleadosService } from '../../service/empleados.service';
import { Empleado } from '../../interface/empleado.interface';

@Component({
  selector: 'app-empleado-detalle',
  standalone: true,
  imports: [CommonModule, RouterLink, Sidebar, Topbar],
  templateUrl: './empleado-detalle.html',
  styleUrl: './empleado-detalle.css',
})
export class EmpleadoDetalle {
  empleado?: Empleado;

  constructor(
    private route: ActivatedRoute,
    private empleadosService: EmpleadosService
  ) {
    this.route.paramMap.subscribe(params => {
      const id = Number(params.get('id'));
      this.empleado = this.empleadosService.obtenerEmpleadoPorId(id);
    });
  }
}