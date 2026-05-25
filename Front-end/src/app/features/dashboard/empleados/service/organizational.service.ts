import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../../../../environments/environment';
import {
  AreaResponse,
  DepartamentoResponse,
  CargoResponse,
} from '../interface/organizational.interface';

@Injectable({ providedIn: 'root' })
export class OrganizationalService {
  private http = inject(HttpClient);
  private apiUrl = environment.apiBaseUrl;

  getAreas(): Observable<AreaResponse[]> {
    return this.http.get<AreaResponse[]>(`${this.apiUrl}/areas`, {
      withCredentials: true,
    });
  }

  getDepartamentos(): Observable<DepartamentoResponse[]> {
    return this.http.get<DepartamentoResponse[]>(`${this.apiUrl}/departamentos`, {
      withCredentials: true,
    });
  }

  getCargos(): Observable<CargoResponse[]> {
    return this.http.get<CargoResponse[]>(`${this.apiUrl}/cargos`, {
      withCredentials: true,
    });
  }
}
