import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../../../../../environments/environment';
import { Area, CreateAreaRequest } from '../interface/area.interface';

@Injectable({
  providedIn: 'root',
})
export class AreasService {
  private http = inject(HttpClient);
  private apiUrl = environment.apiBaseUrl;

  obtenerAreas(): Observable<Area[]> {
    return this.http.get<Area[]>(`${this.apiUrl}/areas`, {
      withCredentials: true,
    });
  }

  obtenerAreaPorId(id: number): Observable<Area> {
    return this.http.get<Area>(`${this.apiUrl}/areas/${id}`, {
      withCredentials: true,
    });
  }

  agregarArea(data: CreateAreaRequest): Observable<Area> {
    return this.http.post<Area>(`${this.apiUrl}/areas`, data, {
      withCredentials: true,
    });
  }

  actualizarArea(id: number, data: CreateAreaRequest): Observable<Area> {
    return this.http.put<Area>(`${this.apiUrl}/areas/${id}`, data, {
      withCredentials: true,
    });
  }

  eliminarArea(id: number): Observable<Area> {
    return this.http.delete<Area>(`${this.apiUrl}/areas/${id}`, {
      withCredentials: true,
    });
  }
}
