import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Sidebar } from '../../../components/sidebar/sidebar';
import { Topbar } from '../../../components/topbar/topbar';
import { EmpleadosService } from '../../service/empleados.service';
import { Empleado } from '../../interface/empleado.interface';

@Component({
  selector: 'app-empleados-home',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, Sidebar, Topbar],
  templateUrl: './empleados-home.html',
  styleUrl: './empleados-home.css',
})
export class EmpleadosHome {
  terminoBusqueda = '';
  empleados: Empleado[] = [];

  constructor(private empleadosService: EmpleadosService) {
    this.cargarEmpleados();
  }

  cargarEmpleados() {
    this.empleados = this.empleadosService.obtenerEmpleados();
  }

  get empleadosFiltrados() {
    const termino = this.terminoBusqueda.toLowerCase().trim();

    if (!termino) {
      return this.empleados;
    }

    return this.empleados.filter((empleado) =>
      empleado.nombre.toLowerCase().includes(termino) ||
      empleado.correo.toLowerCase().includes(termino) ||
      empleado.puesto.toLowerCase().includes(termino) ||
      empleado.direccion.toLowerCase().includes(termino)
    );
  }

  get totalActivos(): number {
    return this.empleados.filter((empleado) => empleado.estado === 'Activo').length;
  }

  get totalInactivos(): number {
    return this.empleados.filter((empleado) => empleado.estado === 'Inactivo').length;
  }

  toggleEstado(empleado: Empleado) {
    const nuevoEstado = empleado.estado === 'Activo' ? 'Inactivo' : 'Activo';

    this.empleadosService.actualizarEmpleado(empleado.id, {
      estado: nuevoEstado,
    });

    this.cargarEmpleados();
  }
}