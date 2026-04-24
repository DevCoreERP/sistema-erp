export interface AreaResponse {
  id: number;
  nombre: string;
  active: boolean;
  createdAt: string;
}

export interface DepartamentoResponse {
  id: number;
  nombre: string;
  active: boolean;
  createdAt: string;
  area: AreaResponse;
}

export interface CargoResponse {
  id: number;
  nombre: string;
  active: boolean;
  createdAt: string;
  departamento: DepartamentoResponse;
}
