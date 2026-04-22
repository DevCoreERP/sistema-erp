import { Component, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-navbar',
  standalone: true, // 👈 ESTO FALTABA
  imports: [RouterLink],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
})
export class Navbar {
  menuOpen = signal(false);

  navLinks = [
    { label: 'Módulos', path: '/inicio' },
    { label: 'Soluciones', path: '/servicios' },
    { label: 'Recursos HR', path: '/nosotros' },
    { label: 'Planes', path: '/contacto' },
  ];

  toggleMenu() {
    this.menuOpen.update((v) => !v);
  }

  closeMenu() {
    this.menuOpen.set(false);
  }
}