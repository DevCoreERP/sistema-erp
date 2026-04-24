import { Departamento } from '../../departamentos/interface/departamento.interface';

export interface Cargo {
  id: number;
  nombre: string;
  active: boolean;
  createdAt: string;
  departamento: Departamento;
}

export interface CreateCargoRequest {
  nombre: string;
  departamentoId: number;
}
