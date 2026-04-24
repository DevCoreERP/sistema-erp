import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs'; // 🔴 IMPORTANTE: Agrega esto

import { Sidebar } from '../../../components/sidebar/sidebar';
import { Topbar } from '../../../components/topbar/topbar';
import { EmpleadosService } from '../../service/empleados.service';
import { OrganizationalService } from '../../service/organizational.service';
import {
  AreaResponse,
  DepartamentoResponse,
  CargoResponse,
} from '../../interface/organizational.interface';

@Component({
  selector: 'app-empleado-form',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, Sidebar, Topbar],
  templateUrl: './empleado-form.html',
  styleUrl: './empleado-form.css',
})
export class EmpleadoForm implements OnInit {
  modoEdicion = false;
  empleadoId!: number;

  private orgService = inject(OrganizationalService);
  private empleadosService = inject(EmpleadosService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private cdr = inject(ChangeDetectorRef);

  areas: AreaResponse[] = [];
  departamentos: DepartamentoResponse[] = [];
  cargos: CargoResponse[] = [];

  areaSeleccionada: number | null = null;
  departamentoSeleccionado: number | null = null;
  cargoSeleccionado: number | null = null;

  empleado = {
    nombreCompleto: '',
    correo: '',
    puesto: '',
    direccion: '',
    telefono: '',
    departamento: '',
    estado: 'Activo' as 'Activo' | 'Inactivo',
  };

  enviado = false;
  imagenPreview: string | ArrayBuffer | null = null;

  ngOnInit() {
    this.inicializarDatosEstaticos(); // Carga usuarios de prueba si está vacío

    // 🔴 1. Cargamos TODA la estructura organizacional al mismo tiempo
    forkJoin({
      areas: this.orgService.getAreas(),
      departamentos: this.orgService.getDepartamentos(),
      cargos: this.orgService.getCargos()
    }).subscribe({
      next: (data) => {
        this.areas = data.areas;
        this.departamentos = data.departamentos;
        this.cargos = data.cargos;

        // 🔴 2. Solo DESPUÉS de tener las áreas, verificamos si es modo edición
        this.verificarModoEdicion();
      },
      error: (err) => console.error("Error cargando estructura", err)
    });
  }

  verificarModoEdicion() {
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
            estado: empleadoExistente.estado as 'Activo' | 'Inactivo',
          };
          this.imagenPreview = empleadoExistente.imagen || null;

          // 🔴 3. MAGIA: Buscamos el cargo exacto en el JSON para sacar los IDs
          const cargoReal = this.cargos.find(c => c.nombre === empleadoExistente.puesto);

          if (cargoReal) {
            // Como tu JSON viene anidado, sacamos los IDs en cadena:
            this.cargoSeleccionado = cargoReal.id;
            this.departamentoSeleccionado = cargoReal.departamento.id;
            this.areaSeleccionada = cargoReal.departamento.area.id;
          }
          this.cdr.detectChanges();
        }
      }
    });
  }

  // --- GETTERS PARA FILTRAR LOS SELECTS ---
  get departamentosFiltrados(): DepartamentoResponse[] {
    if (!this.areaSeleccionada) return [];
    return this.departamentos.filter(d => d.area.id === this.areaSeleccionada);
  }

  get cargosFiltrados(): CargoResponse[] {
    if (!this.departamentoSeleccionado) return [];
    return this.cargos.filter(c => c.departamento.id === this.departamentoSeleccionado);
  }

  get nombreDepartamentoSeleccionado(): string {
    const depto = this.departamentos.find(d => d.id === this.departamentoSeleccionado);
    return depto?.nombre || '';
  }

  get nombreCargoSeleccionado(): string {
    const cargo = this.cargos.find(c => c.id === this.cargoSeleccionado);
    return cargo?.nombre || '';
  }

  // --- LÓGICA DE RESETEO (CASCADA) ---
  onAreaChange() {
    this.departamentoSeleccionado = null;
    this.cargoSeleccionado = null;
    this.empleado.departamento = '';
    this.empleado.puesto = '';
  }

  onDepartamentoChange() {
    this.cargoSeleccionado = null;
    this.empleado.puesto = '';
    // Guardamos el nombre del departamento al seleccionarlo
    this.empleado.departamento = this.nombreDepartamentoSeleccionado;
  }

  onCargoChange() {
    // Guardamos el nombre del cargo al seleccionarlo
    this.empleado.puesto = this.nombreCargoSeleccionado;
  }

  // --- GUARDADO ---
  guardarEmpleado() {
    this.enviado = true;

    if (!this.empleado.nombreCompleto || !this.empleado.correo || !this.cargoSeleccionado || !this.empleado.direccion) {
      return;
    }

    const empleadoData = {
      nombre: this.empleado.nombreCompleto,
      correo: this.empleado.correo,
      puesto: this.nombreCargoSeleccionado,
      direccion: this.empleado.direccion,
      telefono: this.empleado.telefono,
      departamento: this.nombreDepartamentoSeleccionado,
      estado: this.empleado.estado,
      imagen: this.imagenPreview,
    };

    if (this.modoEdicion) {
      this.empleadosService.actualizarEmpleado(this.empleadoId, empleadoData);

      // Actualizamos también en el LocalStorage para reflejarlo en la tabla
      let stored = JSON.parse(localStorage.getItem('empleados') || '[]');
      const index = stored.findIndex((e: any) => e.id === this.empleadoId);
      if(index !== -1) stored[index] = { ...empleadoData, id: this.empleadoId };
      localStorage.setItem('empleados', JSON.stringify(stored));

    } else {
      this.empleadosService.agregarEmpleado(empleadoData);

      const stored = JSON.parse(localStorage.getItem('empleados') || '[]');
      stored.push({ ...empleadoData, id: Date.now() });
      localStorage.setItem('empleados', JSON.stringify(stored));
    }

    this.router.navigate(['/empleados']);
  }

  onImageSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const file = input.files[0];
      const reader = new FileReader();
      reader.onload = () => { this.imagenPreview = reader.result; };
      reader.readAsDataURL(file);
    }
  }

  // 🔴 MOCK DATA: Genera empleados estáticos basados en tu estructura
  private inicializarDatosEstaticos() {
    const stored = localStorage.getItem('empleados');
    if (!stored || JSON.parse(stored).length === 0) {
      const empleadosMuestra = [
        {
          id: 101,
          nombre: 'Carlos Director',
          correo: 'director@erp.com',
          puesto: 'Gerente General (CEO)',
          departamento: 'Gerencia General',
          direccion: 'Av. Empresarial 100',
          telefono: '77700001',
          estado: 'Activo',
          imagen: null
        },
        {
          id: 102,
          nombre: 'Miguel Angel Gutierrez',
          correo: 'miguel@erp.com',
          puesto: 'Desarrollador Frontend',
          departamento: 'Desarrollo de Software',
          direccion: 'Zona Universitaria',
          telefono: '77712345',
          estado: 'Activo',
          imagen: null
        },
        {
          id: 103,
          nombre: 'Ana Finanzas',
          correo: 'ana.f@erp.com',
          puesto: 'Jefe de Finanzas',
          departamento: 'Contabilidad',
          direccion: 'Centro Comercial Sur',
          telefono: '77799888',
          estado: 'Activo',
          imagen: null
        }
      ];
      localStorage.setItem('empleados', JSON.stringify(empleadosMuestra));
    }
  }
}
