import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';

export type EstadoTurno = 'Activo' | 'Inactivo';

export interface Turno {
  id: number;
  nombre: string;
  horaInicio: string;
  horaFin: string;
  descripcion: string;
  estado: EstadoTurno;
}

export interface TurnoRequest {
  nombre: string;
  horaInicio: string;
  horaFin: string;
  descripcion: string;
  estado: EstadoTurno;
}

@Injectable({
  providedIn: 'root',
})
export class TurnosService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiBaseUrl}/turno`;

  listar(): Observable<Turno[]> {
    return this.http.get<Turno[]>(this.apiUrl);
  }

  crear(turno: TurnoRequest): Observable<Turno> {
    return this.http.post<Turno>(this.apiUrl, turno);
  }

  actualizar(id: number, turno: TurnoRequest): Observable<Turno> {
    return this.http.put<Turno>(`${this.apiUrl}/${id}`, turno);
  }

  cambiarEstado(id: number, estadoActual: EstadoTurno): Observable<Turno> {
    const nuevoEstado: EstadoTurno =
      estadoActual === 'Activo' ? 'Inactivo' : 'Activo';

    return this.http.patch<Turno>(`${this.apiUrl}/${id}/estado`, {
      estado: nuevoEstado,
    });
  }
}