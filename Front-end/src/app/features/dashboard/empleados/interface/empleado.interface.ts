export interface Empleado {
  id: number;
  nombre: string;
  correo: string;
  puesto: string;
  direccion: string;
  telefono?: string;
  departamento?: string;
  estado: 'Activo' | 'Inactivo';
  imagen?: string | ArrayBuffer | null;
}