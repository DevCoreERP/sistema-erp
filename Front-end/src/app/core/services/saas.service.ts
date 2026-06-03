import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface PlanResponseDTO {
  id: number;
  nombre: string;
  descripcion: string;
  precioUsd: number;
  limiteUsuarios: number;
  estado: boolean;
  modulos: any[];
  beneficios: any[];
}

export interface SuscripcionResponseDTO {
  id: number;
  tenantId: number;
  planId: number;
  planNombreSnapshot: string;
  tenantMetodoPagoId: number;
  estado: string; // ACTIVA, VENCIDA, PRUEBA, SUSPENDIDA
  tipo: string; // PRUEBA, PAGADA, MANUAL
  fechaInicio: string;
  fechaFin: string;
  fechaProximoVencimiento: string;
  precioUsdSnapshot: number;
  limiteUsuariosSnapshot: number;
  vigente: boolean;
}

export interface PlanAccessResponseDTO {
  planId: number;
  planNombre: string;
  modulos: string[];
  permisos: string[];
}

export interface SuscripcionCompraRequestDTO {
  planId: number;
  tenantMetodoPagoId: number;
}

export interface TenantMetodoPagoRequestDTO {
  metodoPagoId: number; // For the real backend we might need to fetch this or default to 1 (Tarjeta)
  titular: string;
  ultimosDigitos: string;
  marca: string;
  referencia: string;
  esPrincipal: boolean;
  estado: boolean;
}

export interface TenantMetodoPagoResponseDTO {
  id: number;
  tenantId: number;
  metodoPagoId: number;
  metodoPagoNombre: string; // Ej: Tarjeta de Crédito, PayPal
  titular: string;
  ultimosDigitos: string;
  marca: string;
  referencia: string;
  esPrincipal: boolean;
  estado: boolean;
  createdAt: string;
}

export interface PagoResponseDTO {
  id: number;
  suscripcionId: number;
  tenantId: number;
  metodoPagoId: number;
  metodoPagoNombre: string;
  fechaPago: string;
  montoBase: number;
  moneda: string;
  estado: string; // PENDIENTE, PAGADO, FALLIDO, REEMBOLSADO
  referenciaTransaccion: string;
  enlaceFacturaUrl: string;
  notas: string;
}

@Injectable({
  providedIn: 'root'
})
export class SaasService {
  private apiUrl = environment.apiBaseUrl;

  constructor(private http: HttpClient) {}

  // -- Planes --
  getPlanesActivos(): Observable<PlanResponseDTO[]> {
    return this.http.get<PlanResponseDTO[]>(`${this.apiUrl}/planes`);
  }

  obtenerPlan(planId: number): Observable<PlanResponseDTO> {
    return this.http.get<PlanResponseDTO>(`${this.apiUrl}/planes/${planId}`);
  }

  // -- Suscripciones --
  obtenerSuscripcionActual(): Observable<SuscripcionResponseDTO> {
    return this.http.get<SuscripcionResponseDTO>(`${this.apiUrl}/suscripciones/actual`);
  }

  obtenerAccesosActuales(): Observable<PlanAccessResponseDTO> {
    return this.http.get<PlanAccessResponseDTO>(`${this.apiUrl}/suscripciones/actual/accesos`);
  }

  adquirirSuscripcion(dto: SuscripcionCompraRequestDTO): Observable<SuscripcionResponseDTO> {
    return this.http.post<SuscripcionResponseDTO>(`${this.apiUrl}/suscripciones`, dto);
  }

  // -- Métodos de Pago del Tenant --
  listarMetodosPago(): Observable<TenantMetodoPagoResponseDTO[]> {
    return this.http.get<TenantMetodoPagoResponseDTO[]>(`${this.apiUrl}/tenant-metodos-pago`);
  }

  registrarMetodoPago(dto: TenantMetodoPagoRequestDTO): Observable<TenantMetodoPagoResponseDTO> {
    return this.http.post<TenantMetodoPagoResponseDTO>(`${this.apiUrl}/tenant-metodos-pago`, dto);
  }

  actualizarMetodoPago(id: number, dto: TenantMetodoPagoRequestDTO): Observable<TenantMetodoPagoResponseDTO> {
    return this.http.put<TenantMetodoPagoResponseDTO>(`${this.apiUrl}/tenant-metodos-pago/${id}`, dto);
  }

  // -- Historial de Pagos --
  listarHistorialPagos(): Observable<PagoResponseDTO[]> {
    return this.http.get<PagoResponseDTO[]>(`${this.apiUrl}/pagos`);
  }
}
