import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';

export interface Usuario {
  id: number;
  username?: string;
  firstName?: string;
  surnames?: string;
  email?: string;
  phoneNumber?: string;
  estado?: boolean;
  roleNames?: string[];
}

@Injectable({
  providedIn: 'root',
})
export class UsuariosService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiBaseUrl}/auth/usuarios`;

  listar(): Observable<Usuario[]> {
    return this.http.get<Usuario[]>(this.apiUrl);
  }
}