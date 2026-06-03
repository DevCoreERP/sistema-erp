import { Injectable, inject, signal } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap, catchError, of, map, switchMap } from 'rxjs';

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

  login(credentials: LoginRequest, subdomain: string): Observable<any> {
    const headers = new HttpHeaders({
      'X-Tenant-Subdomain': subdomain,
    });

    return this.http
      .post<{ message: string; token: string; tokenType: string }>(
        `${this.apiUrl}/auth/login`,
        credentials,
        {
          headers,
          withCredentials: true,
        }
      )
      .pipe(
        tap((res) => {
          // Store the token from the response body as a fallback
          // in case the HttpOnly cookie doesn't work cross-origin
          if (res.token) {
            localStorage.setItem('auth_token', res.token);
          }
          localStorage.setItem('tenant_subdomain', subdomain);
          this.isAuthenticated.set(true);
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
          localStorage.removeItem('auth_token');
          localStorage.removeItem('tenant_subdomain');
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
      .get(`${this.apiUrl}/auth/me`, {
        withCredentials: true,
        observe: 'response',
      })
      .pipe(
        tap((res: any) => {
          if (res.ok && res.body) {
            this.isAuthenticated.set(true);
            this.currentUser.set(res.body);
          }
        }),
        map((res) => res.ok),
        catchError(() => {
          this.isAuthenticated.set(false);
          this.currentUser.set(null);
          return of(false);
        })
      );
  }
}

