import { Component, HostListener, inject, OnInit } from '@angular/core';
import { NgIf } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../../../core/services/auth.service';

@Component({
  selector: 'app-topbar',
  standalone: true,
  imports: [NgIf, RouterLink],
  templateUrl: './topbar.html',
  styleUrl: './topbar.css',
})
export class Topbar implements OnInit {
  private authService = inject(AuthService);

  menuAbierto = false;

  ngOnInit() {
    this.authService.getUsuario(1).subscribe({
      next: (user) => {
        this.authService.currentUser.set(user);
      },
      error: () => {
        this.authService.currentUser.set(null);
      },
    });
  }

  get nombreCompleto(): string {
    const user = this.authService.currentUser();
    if (user) {
      return `${user.firstName} ${user.surnames}`.trim();
    }
    return 'Usuario';
  }

  get inicial(): string {
    const user = this.authService.currentUser();
    if (user) {
      return user.firstName.charAt(0).toUpperCase();
    }
    return 'U';
  }

  get email(): string {
    const user = this.authService.currentUser();
    return user?.email || 'usuario@empresa.com';
  }

  toggleMenu() {
    this.menuAbierto = !this.menuAbierto;
  }

  cerrarMenu() {
    this.menuAbierto = false;
  }

  logout() {
    this.menuAbierto = false;
    this.authService.logout().subscribe();
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: Event) {
    const target = event.target as HTMLElement;
    if (!target.closest('.user-menu-wrapper')) {
      this.menuAbierto = false;
    }
  }
}
