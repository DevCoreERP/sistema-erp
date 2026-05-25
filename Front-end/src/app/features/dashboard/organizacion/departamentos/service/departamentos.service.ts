import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../../../../../environments/environment';
import { Departamento, CreateDepartamentoRequest } from '../interface/departamento.interface';

@Injectable({
  providedIn: 'root',
})
export class DepartamentosService {
  private http = inject(HttpClient);
  private apiUrl = environment.apiBaseUrl;

  obtenerDepartamentos(): Observable<Departamento[]> {
    return this.http.get<Departamento[]>(`${this.apiUrl}/departamentos`, {
      withCredentials: true,
    });
  }

  obtenerDepartamentoPorId(id: number): Observable<Departamento> {
    return this.http.get<Departamento>(`${this.apiUrl}/departamentos/${id}`, {
      withCredentials: true,
    });
  }

  agregarDepartamento(data: CreateDepartamentoRequest): Observable<Departamento> {
    return this.http.post<Departamento>(`${this.apiUrl}/departamentos`, data, {
      withCredentials: true,
    });
  }

  actualizarDepartamento(id: number, data: CreateDepartamentoRequest): Observable<Departamento> {
    return this.http.put<Departamento>(`${this.apiUrl}/departamentos/${id}`, data, {
      withCredentials: true,
    });
  }

  eliminarDepartamento(id: number): Observable<Departamento> {
    return this.http.delete<Departamento>(`${this.apiUrl}/departamentos/${id}`, {
      withCredentials: true,
    });
  }
}
