import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap, catchError, of, map } from 'rxjs';

import { environment } from '../../../environments/environment';
import {
  LoginRequest,
  LoginResponse,
  AuthUser,
} from '../../shared/interface/auth.interface';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);
  private apiUrl = environment.apiBaseUrl;

  currentUser = signal<AuthUser | null>(null);
  isAuthenticated = signal<boolean>(false);

  login(credentials: LoginRequest): Observable<LoginResponse> {
    return this.http
      .post<LoginResponse>(`${this.apiUrl}/auth/login`, credentials, {
        withCredentials: true,
      })
      .pipe(
       tap((res) => {
          this.isAuthenticated.set(true);
          this.currentUser.set(res.usuario);
          console.log(res);
        })
      );
  }

  logout(): Observable<void> {
    return this.http
      .post<void>(`${this.apiUrl}/auth/logout`, null, {
        withCredentials: true,
      })
      .pipe(
        tap(() => {
          this.currentUser.set(null);
          this.isAuthenticated.set(false);
          this.router.navigate(['/iniciar-sesion']);
        })
      );
  }

  getUsuario(id: number): Observable<AuthUser> {
    return this.http.get<AuthUser>(`${this.apiUrl}/auth/usuarios/${id}`, {
      withCredentials: true,
    });
  }

  checkAuth(): Observable<boolean> {
    return this.http
      .get(`${this.apiUrl}/auth/usuarios/1`, {
        withCredentials: true,
        observe: 'response',
      })
      .pipe(
        tap((res) => {
          if (res.ok) {
            this.isAuthenticated.set(true);
          }
        }),
        map((res) => res.ok),
        catchError(() => {
          this.isAuthenticated.set(false);
          return of(false);
        })
      );
  }
}
