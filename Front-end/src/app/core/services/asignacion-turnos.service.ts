import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';

export type EstadoAsignacionTurno = 'Asignado' | 'Pendiente' | 'Conflicto' | 'Activo' | 'Inactivo';

export interface AsignacionTurno {
  id: number;
  usuarioId?: number;
  empleadoId?: number;
  turnoId: number;
  fechaInicio?: string;
  fechaFin?: string;
  fechaI?: string;
  fechaF?: string;
  estado: EstadoAsignacionTurno;
}

export interface AsignacionTurnoRequest {
  usuarioId: number;
  turnoId: number;
  fechaInicio: string;
  fechaFin: string;
}

@Injectable({
  providedIn: 'root',
})
export class AsignacionTurnosService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiBaseUrl}/control-asistencia/asignaciones-turno`;

  listar(): Observable<AsignacionTurno[]> {
    return this.http.get<AsignacionTurno[]>(this.apiUrl);
  }

  crear(asignacion: AsignacionTurnoRequest): Observable<AsignacionTurno> {
    return this.http.post<AsignacionTurno>(this.apiUrl, asignacion);
  }

  actualizar(id: number, asignacion: AsignacionTurnoRequest): Observable<AsignacionTurno> {
    return this.http.put<AsignacionTurno>(`${this.apiUrl}/${id}`, asignacion);
  }

  cambiarEstado(id: number, estado: string): Observable<AsignacionTurno> {
    return this.http.patch<AsignacionTurno>(`${this.apiUrl}/${id}/estado`, {
      estado,
    });
  }

  finalizar(id: number): Observable<AsignacionTurno> {
    return this.http.patch<AsignacionTurno>(`${this.apiUrl}/${id}/finalizar`, {});
  }
}