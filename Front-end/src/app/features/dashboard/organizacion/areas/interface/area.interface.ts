export interface Area {
  id: number;
  nombre: string;
  active: boolean;
  createdAt: string;
}

export interface CreateAreaRequest {
  nombre: string;
}
