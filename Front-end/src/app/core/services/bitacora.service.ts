import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError, delay } from 'rxjs/operators';
import { environment } from '../../../environments/environment';

export interface BitacoraRegistro {
  id: number;
  ip: string;
  usuario: string | null;
  tenant: string | null;
  endpoint: string;
  httpStatus: number;
  createdAt: string;
}

export interface PageBitacora {
  content: BitacoraRegistro[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

@Injectable({
  providedIn: 'root'
})
export class BitacoraService {
  private apiUrl = `${environment.apiBaseUrl}/bitacora`;

  constructor(private http: HttpClient) {}

  obtenerRegistros(page: number = 0, size: number = 10): Observable<PageBitacora> {
    return this.http.get<PageBitacora>(`${this.apiUrl}?page=${page}&size=${size}`).pipe(
      catchError(() => {
        // Fallback Mock porque el backend aún no tiene el endpoint
        console.warn('Backend endpoint /bitacora falló. Usando mock local para UI.');
        return of(this.generarMockBitacora(page, size)).pipe(delay(600));
      })
    );
  }

  private generarMockBitacora(page: number, size: number): PageBitacora {
    const totalElements = 145;
    const registros: BitacoraRegistro[] = [];
    
    const endpoints = [
      '/v1/auth/login', '/v1/auth/me', '/v1/suscripciones/actual', 
      '/v1/usuarios', '/v1/empleados/list', '/v1/planes/activos', 
      '/v1/pagos/checkout', '/v1/tenant/provision'
    ];
    
    const usuarios = ['admin@erp.com', 'rrhh@empresa.com', 'j.doe@correo.com', 'sysadmin'];
    const tenants = ['devcore', 'acme_corp', 'demo_tenant', 'tech_solutions'];
    
    for (let i = 0; i < size; i++) {
      const id = totalElements - (page * size + i);
      if (id <= 0) break;
      
      const isError = Math.random() > 0.85;
      const status = isError ? (Math.random() > 0.5 ? 401 : (Math.random() > 0.5 ? 403 : 500)) : (Math.random() > 0.8 ? 201 : 200);
      
      registros.push({
        id: id,
        ip: `192.168.1.${Math.floor(Math.random() * 255)}`,
        usuario: Math.random() > 0.2 ? usuarios[Math.floor(Math.random() * usuarios.length)] : null,
        tenant: Math.random() > 0.1 ? tenants[Math.floor(Math.random() * tenants.length)] : null,
        endpoint: endpoints[Math.floor(Math.random() * endpoints.length)],
        httpStatus: status,
        createdAt: new Date(Date.now() - Math.floor(Math.random() * 10000000000)).toISOString()
      });
    }

    // Ordenar por fecha descendente
    registros.sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime());

    return {
      content: registros,
      totalElements: totalElements,
      totalPages: Math.ceil(totalElements / size),
      size: size,
      number: page
    };
  }
}
