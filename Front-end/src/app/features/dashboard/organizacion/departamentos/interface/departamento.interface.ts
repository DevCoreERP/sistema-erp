import { Area } from '../../areas/interface/area.interface';

export interface Departamento {
  id: number;
  nombre: string;
  active: boolean;
  createdAt: string;
  area: Area;
}

export interface CreateDepartamentoRequest {
  nombre: string;
  areaId: number;
}
