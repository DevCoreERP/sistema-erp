import { Injectable } from '@angular/core';
import { Area } from '../interface/area.interface';

@Injectable({
  providedIn: 'root',
})
export class AreasService {
  private areas: Area[] = [
    {
      id: 1,
      nombre: 'Recursos Humanos',
      descripcion: 'Gestión del personal',
      estado: 'Activa',
    },
    {
      id: 2,
      nombre: 'Contabilidad',
      descripcion: 'Finanzas y contabilidad',
      estado: 'Activa',
    },
    {
      id: 3,
      nombre: 'Sistemas',
      descripcion: 'Tecnología e infraestructura',
      estado: 'Inactiva',
    },
  ];

  private contadorId = 4;

  obtenerAreas(): Area[] {
  return [...this.areas];
}

  obtenerAreaPorId(id: number): Area | undefined {
    return this.areas.find((a) => a.id === id);
  }

  agregarArea(data: Omit<Area, 'id'>): void {
    const nueva: Area = {
      ...data,
      id: this.contadorId++,
    };

    this.areas.push(nueva);
  }

  actualizarArea(id: number, data: Partial<Area>): void {
    const index = this.areas.findIndex((a) => a.id === id);

    if (index !== -1) {
      this.areas[index] = {
        ...this.areas[index],
        ...data,
      };
    }
  }

  cambiarEstado(id: number): void {
    const area = this.obtenerAreaPorId(id);

    if (area) {
      area.estado = area.estado === 'Activa' ? 'Inactiva' : 'Activa';
    }
  }
}