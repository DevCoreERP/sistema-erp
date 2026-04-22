import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { Sidebar } from '../../../components/sidebar/sidebar';
import { Topbar } from '../../../components/topbar/topbar';
import { EmpleadosService } from '../../service/empleados.service';

@Component({
  selector: 'app-empleado-form',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, Sidebar, Topbar],
  templateUrl: './empleado-form.html',
  styleUrl: './empleado-form.css',
})
export class EmpleadoForm {
  modoEdicion = false;
  empleadoId!: number;

  empleado: {
    nombreCompleto: string;
    correo: string;
    puesto: string;
    direccion: string;
    telefono: string;
    departamento: string;
    estado: 'Activo' | 'Inactivo';
  } = {
    nombreCompleto: '',
    correo: '',
    puesto: '',
    direccion: '',
    telefono: '',
    departamento: '',
    estado: 'Activo',
  };

  enviado = false;
  imagenPreview: string | ArrayBuffer | null = null;

  constructor(
    private empleadosService: EmpleadosService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.route.paramMap.subscribe((params) => {
      const idParam = params.get('id');

      if (idParam) {
        this.modoEdicion = true;
        this.empleadoId = Number(idParam);

        const empleadoExistente = this.empleadosService.obtenerEmpleadoPorId(this.empleadoId);

        if (empleadoExistente) {
          this.empleado = {
            nombreCompleto: empleadoExistente.nombre,
            correo: empleadoExistente.correo,
            puesto: empleadoExistente.puesto,
            direccion: empleadoExistente.direccion,
            telefono: empleadoExistente.telefono || '',
            departamento: empleadoExistente.departamento || '',
            estado: empleadoExistente.estado,
          };

          this.imagenPreview = empleadoExistente.imagen || null;
        }
      }
    });
  }

  onImageSelected(event: Event) {
    const input = event.target as HTMLInputElement;

    if (input.files && input.files.length > 0) {
      const file = input.files[0];
      const reader = new FileReader();

      reader.onload = () => {
        this.imagenPreview = reader.result;
      };

      reader.readAsDataURL(file);
    }
  }

  guardarEmpleado() {
    this.enviado = true;

    if (
      !this.empleado.nombreCompleto ||
      !this.empleado.correo ||
      !this.empleado.puesto ||
      !this.empleado.direccion
    ) {
      return;
    }

    if (this.modoEdicion) {
      this.empleadosService.actualizarEmpleado(this.empleadoId, {
        nombre: this.empleado.nombreCompleto,
        correo: this.empleado.correo,
        puesto: this.empleado.puesto,
        direccion: this.empleado.direccion,
        telefono: this.empleado.telefono,
        departamento: this.empleado.departamento,
        estado: this.empleado.estado,
        imagen: this.imagenPreview,
      });

      alert('Empleado actualizado correctamente');
    } else {
      this.empleadosService.agregarEmpleado({
        nombre: this.empleado.nombreCompleto,
        correo: this.empleado.correo,
        puesto: this.empleado.puesto,
        direccion: this.empleado.direccion,
        telefono: this.empleado.telefono,
        departamento: this.empleado.departamento,
        estado: this.empleado.estado,
        imagen: this.imagenPreview,
      });

      alert('Empleado guardado correctamente');
    }

    this.router.navigate(['/empleados']);
  }
}