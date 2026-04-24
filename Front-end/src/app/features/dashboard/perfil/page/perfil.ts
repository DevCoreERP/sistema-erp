import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Topbar } from "../../components/topbar/topbar";
import { Sidebar } from "../../components/sidebar/sidebar";
import { RouterLink } from '@angular/router';
import { Empleado } from '../../empleados/interface/empleado.interface';

@Component({
  selector: 'app-perfil',
  standalone: true,
  imports: [Topbar, Sidebar,FormsModule, CommonModule, RouterLink],
  templateUrl: './perfil.html',
  styleUrl: './perfil.css'
})
export class PerfilPage {

  guardarEmpleado  = signal(false);

  imagenPreview: string | ArrayBuffer | null = null;

  empleado: Empleado = {
      id: 1,
      nombre: 'Juan Pérez',
      correo: 'juan.perez@empresa.com',
      puesto: 'Analista de RRHH',
      direccion: 'Santa Cruz, Bolivia',
      estado: 'Activo',
  }
}
