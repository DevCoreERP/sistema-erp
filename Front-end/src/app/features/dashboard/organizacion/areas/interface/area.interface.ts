export interface Area {
  id: number;
  nombre: string;
  descripcion?: string;
  estado: 'Activa' | 'Inactiva';
}