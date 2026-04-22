import { Component, HostListener } from '@angular/core';
import { NgIf } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-topbar',
  standalone: true,
  imports: [NgIf, RouterLink],
  templateUrl: './topbar.html',
  styleUrl: './topbar.css',
})
export class Topbar {
  usuario: any = {};

constructor() {
  try {
    this.usuario = JSON.parse(localStorage.getItem('usuario') || '{}');
  } catch {
    this.usuario = {};
  }
}
  menuAbierto = false;

  get nombreCompleto(): string {
    return `${this.usuario.nombre || 'Usuario'} ${this.usuario.apellido || ''}`.trim();
  }

  get inicial(): string {
    return (this.usuario.nombre?.charAt(0) || 'U').toUpperCase();
  }

  toggleMenu() {
    this.menuAbierto = !this.menuAbierto;
  }

  cerrarMenu() {
    this.menuAbierto = false;
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: Event) {
    const target = event.target as HTMLElement;
    if (!target.closest('.user-menu-wrapper')) {
      this.menuAbierto = false;
    }
  }
}