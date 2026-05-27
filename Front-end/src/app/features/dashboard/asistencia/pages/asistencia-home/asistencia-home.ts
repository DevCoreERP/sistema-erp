import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { Sidebar } from '../../../components/sidebar/sidebar';
import { Topbar } from '../../../components/topbar/topbar';

type EstadoAsistencia = 'Presente' | 'Tarde' | 'Ausente' | 'Permiso';

interface AsistenciaRegistro {
  id: number;
  empleado: string;
  departamento: string;
  fecha: string;
  horaEntrada: string;
  horaSalida: string;
  estado: EstadoAsistencia;
  observacion: string;
}

@Component({
  selector: 'app-asistencia-home',
  standalone: true,
  imports: [CommonModule, FormsModule, Sidebar, Topbar],
  templateUrl: './asistencia-home.html',
  styleUrl: './asistencia-home.css',
})
export class AsistenciaHome {
  asistencias: AsistenciaRegistro[] = [];

  empleado = '';
  departamento = '';
  fecha = '';
  horaEntrada = '';
  horaSalida = '';
  estado: EstadoAsistencia | '' = '';
  observacion = '';

  busqueda = '';
  filtroEstado: EstadoAsistencia | '' = '';
  filtroFecha = '';

  modoFormulario: 'crear' | 'editar' = 'crear';
  registroEditandoId: number | null = null;

  registroSeleccionado: AsistenciaRegistro | null = null;

  mensaje = '';
  tipoMensaje: 'ok' | 'error' | '' = '';

  estadosAsistencia: EstadoAsistencia[] = [
    'Presente',
    'Tarde',
    'Ausente',
    'Permiso',
  ];

  private nextId = 1;
  private mensajeTimeout: ReturnType<typeof setTimeout> | null = null;

  get asistenciasFiltradas(): AsistenciaRegistro[] {
    const termino = this.busqueda.toLowerCase().trim();

    return this.asistencias.filter((asistencia) => {
      const coincideTexto =
        !termino ||
        asistencia.empleado.toLowerCase().includes(termino) ||
        asistencia.departamento.toLowerCase().includes(termino);

      const coincideEstado =
        !this.filtroEstado || asistencia.estado === this.filtroEstado;

      const coincideFecha =
        !this.filtroFecha || asistencia.fecha === this.filtroFecha;

      return coincideTexto && coincideEstado && coincideFecha;
    });
  }

  get totalRegistros(): number {
    return this.asistencias.length;
  }

  get totalPresentes(): number {
    return this.asistencias.filter(
      (asistencia) => asistencia.estado === 'Presente'
    ).length;
  }

  get totalTardanzas(): number {
    return this.asistencias.filter(
      (asistencia) => asistencia.estado === 'Tarde'
    ).length;
  }

  get totalAusentes(): number {
    return this.asistencias.filter(
      (asistencia) => asistencia.estado === 'Ausente'
    ).length;
  }

  get totalPermisos(): number {
    return this.asistencias.filter(
      (asistencia) => asistencia.estado === 'Permiso'
    ).length;
  }

  registrarAsistencia(): void {
    if (!this.empleado.trim()) {
      this.mostrarMensaje('El campo Empleado es obligatorio.', 'error');
      return;
    }

    if (!this.departamento.trim()) {
      this.mostrarMensaje('El campo Departamento es obligatorio.', 'error');
      return;
    }

    if (!this.fecha) {
      this.mostrarMensaje('El campo Fecha es obligatorio.', 'error');
      return;
    }

    if (!this.estado) {
      this.mostrarMensaje('El campo Estado es obligatorio.', 'error');
      return;
    }

    if (
      (this.estado === 'Presente' || this.estado === 'Tarde') &&
      !this.horaEntrada
    ) {
      this.mostrarMensaje(
        `La hora de entrada es obligatoria cuando el estado es ${this.estado}.`,
        'error'
      );
      return;
    }

    const existeDuplicado = this.asistencias.some((asistencia) => {
      const mismoEmpleado =
        asistencia.empleado.toLowerCase().trim() ===
        this.empleado.toLowerCase().trim();

      const mismaFecha = asistencia.fecha === this.fecha;

      const noEsElMismoRegistro =
        asistencia.id !== this.registroEditandoId;

      return mismoEmpleado && mismaFecha && noEsElMismoRegistro;
    });

    if (existeDuplicado) {
      this.mostrarMensaje(
        'Ya existe un registro de asistencia para este empleado en la fecha seleccionada.',
        'error'
      );
      return;
    }

    if (this.modoFormulario === 'crear') {
      this.crearRegistro();
      return;
    }

    this.actualizarRegistro();
  }

  crearRegistro(): void {
    if (!this.estado) {
      return;
    }

    const nuevoRegistro: AsistenciaRegistro = {
      id: this.nextId++,
      empleado: this.empleado.trim(),
      departamento: this.departamento.trim(),
      fecha: this.fecha,
      horaEntrada: this.horaEntrada,
      horaSalida: this.horaSalida,
      estado: this.estado,
      observacion: this.observacion.trim(),
    };

    this.asistencias = [...this.asistencias, nuevoRegistro];

    this.limpiarFormulario();
    this.mostrarMensaje('Asistencia registrada correctamente.', 'ok');
  }

  actualizarRegistro(): void {
    if (!this.estado || this.registroEditandoId === null) {
      this.mostrarMensaje('No se encontró el registro que desea editar.', 'error');
      return;
    }

    this.asistencias = this.asistencias.map((asistencia) => {
      if (asistencia.id !== this.registroEditandoId) {
        return asistencia;
      }

      return {
        ...asistencia,
        empleado: this.empleado.trim(),
        departamento: this.departamento.trim(),
        fecha: this.fecha,
        horaEntrada: this.horaEntrada,
        horaSalida: this.horaSalida,
        estado: this.estado as EstadoAsistencia,
        observacion: this.observacion.trim(),
      };
    });

    if (this.registroSeleccionado?.id === this.registroEditandoId) {
      const registroActualizado = this.asistencias.find(
        (asistencia) => asistencia.id === this.registroEditandoId
      );

      this.registroSeleccionado = registroActualizado ?? null;
    }

    this.limpiarFormulario();
    this.mostrarMensaje('Registro actualizado correctamente.', 'ok');
  }

  editarRegistro(registro: AsistenciaRegistro): void {
    this.modoFormulario = 'editar';
    this.registroEditandoId = registro.id;

    this.empleado = registro.empleado;
    this.departamento = registro.departamento;
    this.fecha = registro.fecha;
    this.horaEntrada = registro.horaEntrada;
    this.horaSalida = registro.horaSalida;
    this.estado = registro.estado;
    this.observacion = registro.observacion;

    this.cerrarDetalle();

    window.scrollTo({
      top: 0,
      behavior: 'smooth',
    });
  }

  verRegistro(registro: AsistenciaRegistro): void {
    this.registroSeleccionado = registro;
  }

  cerrarDetalle(): void {
    this.registroSeleccionado = null;
  }

  anularRegistro(id: number): void {
    this.asistencias = this.asistencias.filter(
      (asistencia) => asistencia.id !== id
    );

    if (this.registroEditandoId === id) {
      this.limpiarFormulario();
    }

    if (this.registroSeleccionado?.id === id) {
      this.cerrarDetalle();
    }

    this.mostrarMensaje('Registro anulado correctamente.', 'ok');
  }

  cancelarEdicion(): void {
    this.limpiarFormulario();
    this.mostrarMensaje('Edición cancelada.', 'ok');
  }

  limpiarFormulario(): void {
    this.empleado = '';
    this.departamento = '';
    this.fecha = '';
    this.horaEntrada = '';
    this.horaSalida = '';
    this.estado = '';
    this.observacion = '';

    this.modoFormulario = 'crear';
    this.registroEditandoId = null;
  }

  limpiarFiltros(): void {
    this.busqueda = '';
    this.filtroEstado = '';
    this.filtroFecha = '';
  }

  mostrarMensaje(texto: string, tipo: 'ok' | 'error'): void {
    this.mensaje = texto;
    this.tipoMensaje = tipo;

    if (this.mensajeTimeout) {
      clearTimeout(this.mensajeTimeout);
    }

    this.mensajeTimeout = setTimeout(() => {
      this.mensaje = '';
      this.tipoMensaje = '';
    }, 4000);
  }

  trackById(index: number, registro: AsistenciaRegistro): number {
    return registro.id;
  }
}