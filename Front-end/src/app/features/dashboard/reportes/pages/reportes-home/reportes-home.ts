import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { Sidebar } from '../../../components/sidebar/sidebar';

export interface PlantillaReporte {
  id: number;
  nombre: string;
  modulo: string;
  descripcion: string;
  estado: string;
  icono: string;
}

export interface ReporteGenerado {
  codigo: string;
  nombre: string;
  modulo: string;
  departamento: string;
  estado: string;
  fechaGeneracion: string;
  responsable: string;
  descripcion: string;
  filtrosAplicados: string;
}

export interface FiltrosReporte {
  fechaDesde: string;
  fechaHasta: string;
  modulo: string;
  estado: string;
  departamento: string;
  tipoReporte: string;
  textoBusqueda: string;
}

export interface IndicadorReporte {
  etiqueta: string;
  valor: number;
  icono: string;
  color: string;
}

export interface AnaliticaReporte {
  categoria: string;
  valor: number;
  porcentaje: number;
  color: string;
}

@Component({
  selector: 'app-reportes-home',
  standalone: true,
  imports: [CommonModule, FormsModule, Sidebar],
  templateUrl: './reportes-home.html',
  styleUrls: ['./reportes-home.css']
})
export class ReportesHomeComponent implements OnInit {

  filtros: FiltrosReporte = {
    fechaDesde: '',
    fechaHasta: '',
    modulo: '',
    estado: '',
    departamento: '',
    tipoReporte: '',
    textoBusqueda: ''
  };

  plantillas: PlantillaReporte[] = [
    {
      id: 1,
      nombre: 'Reporte de Empleados',
      modulo: 'Usuarios',
      descripcion: 'Listado completo de usuarios registrados con datos de contrato, rol y departamento.',
      estado: 'Activo',
      icono: '👤'
    },
    {
      id: 2,
      nombre: 'Reporte de Asistencia',
      modulo: 'Asistencia',
      descripcion: 'Registro de asistencias diarias, tardanzas y ausencias por empleado y período.',
      estado: 'Activo',
      icono: '📋'
    },
    {
      id: 3,
      nombre: 'Reporte de Permisos',
      modulo: 'Permisos',
      descripcion: 'Solicitudes de permiso por tipo, estado y responsable durante el período seleccionado.',
      estado: 'Activo',
      icono: '📝'
    },
    {
      id: 4,
      nombre: 'Reporte de Vacaciones',
      modulo: 'Vacaciones',
      descripcion: 'Saldo, consumo y solicitudes de vacaciones por empleado y departamento.',
      estado: 'Activo',
      icono: '🏖️'
    },
    {
      id: 5,
      nombre: 'Reporte de Contratos',
      modulo: 'Contratos',
      descripcion: 'Estado y vencimiento de contratos activos, tipos y distribución por departamento.',
      estado: 'Activo',
      icono: '📄'
    },
    {
      id: 6,
      nombre: 'Reporte de Departamentos',
      modulo: 'Departamentos',
      descripcion: 'Estructura organizacional, cantidad de empleados por departamento y responsables.',
      estado: 'Activo',
      icono: '🏢'
    },
    {
      id: 7,
      nombre: 'Planilla General de Nómina',
      modulo: 'Nóminas',
      descripcion: 'Resumen gerencial de nómina por período: haberes, descuentos y totales por departamento.',
      estado: 'En revisión',
      icono: '💰'
    }
  ];

  reportesGenerados: ReporteGenerado[] = [
    {
      codigo: 'REP-DEMO-001',
      nombre: 'Empleados Activos - Demo',
      modulo: 'Usuarios',
      departamento: 'Tecnología',
      estado: 'Completado',
      fechaGeneracion: '2026-06-02',
      responsable: 'Usuario RRHH',
      descripcion: 'Reporte demo de usuarios activos asociados a contrato, rol y departamento.',
      filtrosAplicados: 'Módulo: Usuarios | Departamento: Tecnología | Estado: Completado'
    },
    {
      codigo: 'REP-DEMO-002',
      nombre: 'Asistencias Registradas - Demo',
      modulo: 'Asistencia',
      departamento: 'Operaciones',
      estado: 'Completado',
      fechaGeneracion: '2026-06-02',
      responsable: 'Usuario RRHH',
      descripcion: 'Reporte demo de registros de asistencia por departamento y período.',
      filtrosAplicados: 'Módulo: Asistencia | Departamento: Operaciones | Estado: Completado'
    },
    {
      codigo: 'REP-DEMO-003',
      nombre: 'Permisos Pendientes - Demo',
      modulo: 'Permisos',
      departamento: 'Recursos Humanos',
      estado: 'Pendiente',
      fechaGeneracion: '2026-06-02',
      responsable: 'Usuario RRHH',
      descripcion: 'Reporte demo de solicitudes de permiso filtradas por estado de solicitud.',
      filtrosAplicados: 'Módulo: Permisos | Estado: Pendiente | Departamento: Recursos Humanos'
    },
    {
      codigo: 'REP-DEMO-004',
      nombre: 'Contratos Vigentes - Demo',
      modulo: 'Contratos',
      departamento: 'Administración',
      estado: 'En proceso',
      fechaGeneracion: '2026-06-02',
      responsable: 'Usuario RRHH',
      descripcion: 'Reporte demo de contratos vigentes clasificados por tipo de contrato y departamento.',
      filtrosAplicados: 'Módulo: Contratos | Departamento: Administración | Estado: En proceso'
    }
  ];

  reportesFiltrados: ReporteGenerado[] = [];
  reporteSeleccionado: ReporteGenerado | null = null;
  modalAbierto = false;
  notificacion: string | null = null;

  indicadores: IndicadorReporte[] = [];

  analiticas: AnaliticaReporte[] = [
    { categoria: 'Solicitudes aprobadas', valor: 0, porcentaje: 0, color: '#22c55e' },
    { categoria: 'Solicitudes pendientes', valor: 1, porcentaje: 100, color: '#f59e0b' },
    { categoria: 'Solicitudes rechazadas', valor: 0, porcentaje: 0, color: '#ef4444' }
  ];

  analiticasModulo: AnaliticaReporte[] = [
    { categoria: 'Permisos', valor: 1, porcentaje: 34, color: '#6366f1' },
    { categoria: 'Vacaciones', valor: 0, porcentaje: 0, color: '#0ea5e9' },
    { categoria: 'Asistencia', valor: 1, porcentaje: 33, color: '#8b5cf6' },
    { categoria: 'Contratos', valor: 1, porcentaje: 33, color: '#2563eb' }
  ];

  analiticasContrato: AnaliticaReporte[] = [
    { categoria: 'Indefinido', valor: 1, porcentaje: 50, color: '#2563eb' },
    { categoria: 'Plazo fijo', valor: 1, porcentaje: 50, color: '#0891b2' },
    { categoria: 'Por obra', valor: 0, porcentaje: 0, color: '#64748b' }
  ];

  analiticasDepartamento: AnaliticaReporte[] = [
    { categoria: 'Tecnología', valor: 1, porcentaje: 25, color: '#7c3aed' },
    { categoria: 'Operaciones', valor: 1, porcentaje: 25, color: '#0284c7' },
    { categoria: 'Recursos Humanos', valor: 1, porcentaje: 25, color: '#059669' },
    { categoria: 'Administración', valor: 1, porcentaje: 25, color: '#d97706' }
  ];

  tendenciaMensual: AnaliticaReporte[] = [
    { categoria: 'Feb', valor: 0, porcentaje: 0, color: '#2563eb' },
    { categoria: 'Mar', valor: 0, porcentaje: 0, color: '#2563eb' },
    { categoria: 'Abr', valor: 0, porcentaje: 0, color: '#2563eb' },
    { categoria: 'May', valor: 0, porcentaje: 0, color: '#2563eb' },
    { categoria: 'Jun', valor: 4, porcentaje: 100, color: '#2563eb' }
  ];

  ngOnInit(): void {
    this.reportesFiltrados = [...this.reportesGenerados];
    this.calcularResumen();
  }

  calcularResumen(): void {
    const usuariosRegistrados = this.reportesGenerados.filter(
      reporte => reporte.modulo === 'Usuarios'
    ).length;

    const contratosVigentes = this.reportesGenerados.filter(
      reporte => reporte.modulo === 'Contratos'
    ).length;

    const asistenciasRegistradas = this.reportesGenerados.filter(
      reporte => reporte.modulo === 'Asistencia'
    ).length;

    const solicitudesPermiso = this.reportesGenerados.filter(
      reporte => reporte.modulo === 'Permisos'
    ).length;

    const solicitudesVacacion = this.reportesGenerados.filter(
      reporte => reporte.modulo === 'Vacaciones'
    ).length;

    const departamentosActivos = new Set(
      this.reportesGenerados
        .map(reporte => reporte.departamento)
        .filter(departamento => departamento && departamento !== 'Todos')
    ).size;

    this.indicadores = [
      {
        etiqueta: 'Usuarios registrados',
        valor: usuariosRegistrados,
        icono: '👥',
        color: '#2563eb'
      },
      {
        etiqueta: 'Contratos vigentes',
        valor: contratosVigentes,
        icono: '📄',
        color: '#0891b2'
      },
      {
        etiqueta: 'Asistencias registradas',
        valor: asistenciasRegistradas,
        icono: '✅',
        color: '#059669'
      },
      {
        etiqueta: 'Solicitudes de permiso',
        valor: solicitudesPermiso,
        icono: '📝',
        color: '#d97706'
      },
      {
        etiqueta: 'Solicitudes de vacación',
        valor: solicitudesVacacion,
        icono: '🏖️',
        color: '#7c3aed'
      },
      {
        etiqueta: 'Departamentos activos',
        valor: departamentosActivos,
        icono: '🏢',
        color: '#0f172a'
      },
      {
        etiqueta: 'Reportes disponibles',
        valor: this.plantillas.length,
        icono: '📊',
        color: '#2563eb'
      }
    ];
  }

  obtenerReportesFiltrados(): ReporteGenerado[] {
    return this.reportesGenerados.filter((reporte) => {
      const textoBusqueda = this.filtros.textoBusqueda.trim().toLowerCase();

      const coincideModulo =
        !this.filtros.modulo ||
        reporte.modulo.toLowerCase().includes(this.filtros.modulo.toLowerCase());

      const coincideEstado =
        !this.filtros.estado ||
        reporte.estado.toLowerCase() === this.filtros.estado.toLowerCase();

      const coincideDepartamento =
        !this.filtros.departamento ||
        reporte.departamento.toLowerCase().includes(this.filtros.departamento.toLowerCase());

      const coincideTipo =
        !this.filtros.tipoReporte ||
        reporte.nombre.toLowerCase().includes(this.filtros.tipoReporte.toLowerCase()) ||
        reporte.modulo.toLowerCase().includes(this.filtros.tipoReporte.toLowerCase());

      const coincideTexto =
        !textoBusqueda ||
        reporte.codigo.toLowerCase().includes(textoBusqueda) ||
        reporte.nombre.toLowerCase().includes(textoBusqueda) ||
        reporte.modulo.toLowerCase().includes(textoBusqueda) ||
        reporte.departamento.toLowerCase().includes(textoBusqueda) ||
        reporte.estado.toLowerCase().includes(textoBusqueda) ||
        reporte.responsable.toLowerCase().includes(textoBusqueda);

      const coincideFechaDesde =
        !this.filtros.fechaDesde ||
        reporte.fechaGeneracion >= this.filtros.fechaDesde;

      const coincideFechaHasta =
        !this.filtros.fechaHasta ||
        reporte.fechaGeneracion <= this.filtros.fechaHasta;

      return (
        coincideModulo &&
        coincideEstado &&
        coincideDepartamento &&
        coincideTipo &&
        coincideTexto &&
        coincideFechaDesde &&
        coincideFechaHasta
      );
    });
  }

  aplicarFiltros(): void {
    this.reportesFiltrados = this.obtenerReportesFiltrados();
  }

  limpiarFiltros(): void {
    this.filtros = {
      fechaDesde: '',
      fechaHasta: '',
      modulo: '',
      estado: '',
      departamento: '',
      tipoReporte: '',
      textoBusqueda: ''
    };

    this.reportesFiltrados = [...this.reportesGenerados];
  }

  verReporte(reporte: ReporteGenerado): void {
    this.reporteSeleccionado = reporte;
    this.modalAbierto = true;
  }

  cerrarModal(): void {
    this.modalAbierto = false;
    this.reporteSeleccionado = null;
  }

  exportarReporte(reporte: ReporteGenerado): void {
    this.notificacion = `Exportación preparada para el reporte: "${reporte.nombre}" (${reporte.codigo}).`;

    setTimeout(() => {
      this.limpiarNotificacion();
    }, 3500);
  }

  seleccionarPlantilla(plantilla: PlantillaReporte): void {
    const reporteDemo: ReporteGenerado = {
      codigo: `TPL-${plantilla.id.toString().padStart(3, '0')}`,
      nombre: plantilla.nombre,
      modulo: plantilla.modulo,
      departamento: 'General',
      estado: plantilla.estado,
      fechaGeneracion: 'Sin generar',
      responsable: 'Usuario RRHH',
      descripcion: plantilla.descripcion,
      filtrosAplicados: 'Plantilla predefinida pendiente de generación con filtros QBE.'
    };

    this.verReporte(reporteDemo);
  }

  limpiarNotificacion(): void {
    this.notificacion = null;
  }
}