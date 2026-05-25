import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../../../../../environments/environment';
import { Cargo, CreateCargoRequest } from '../interface/cargo.interface';

@Injectable({
  providedIn: 'root',
})
export class CargosService {
  private http = inject(HttpClient);
  private apiUrl = environment.apiBaseUrl;

  obtenerCargos(): Observable<Cargo[]> {
    return this.http.get<Cargo[]>(`${this.apiUrl}/cargos`, {
      withCredentials: true,
    });
  }

  obtenerCargoPorId(id: number): Observable<Cargo> {
    return this.http.get<Cargo>(`${this.apiUrl}/cargos/${id}`, {
      withCredentials: true,
    });
  }

  agregarCargo(data: CreateCargoRequest): Observable<Cargo> {
    return this.http.post<Cargo>(`${this.apiUrl}/cargos`, data, {
      withCredentials: true,
    });
  }

  actualizarCargo(id: number, data: CreateCargoRequest): Observable<Cargo> {
    return this.http.put<Cargo>(`${this.apiUrl}/cargos/${id}`, data, {
      withCredentials: true,
    });
  }

  eliminarCargo(id: number): Observable<Cargo> {
    return this.http.delete<Cargo>(`${this.apiUrl}/cargos/${id}`, {
      withCredentials: true,
    });
  }
}
