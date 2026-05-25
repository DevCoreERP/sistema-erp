import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Topbar } from "../../components/topbar/topbar";
import { Sidebar } from "../../components/sidebar/sidebar";
import { RouterLink } from '@angular/router';
import { AuthService } from '../../../../core/services/auth.service';
import { AuthUser } from '../../../../shared/interface/auth.interface';

@Component({
  selector: 'app-perfil',
  standalone: true,
  imports: [Topbar, Sidebar,FormsModule, CommonModule, RouterLink],
  templateUrl: './perfil.html',
  styleUrl: './perfil.css'
})
export class PerfilPage implements OnInit {

  private authService = inject(AuthService);

  guardarEmpleado  = signal(false);

  imagenPreview: string | ArrayBuffer | null = null;

  usuario = signal<AuthUser | null>(null);

  ngOnInit() {
    this.authService.getUsuario(1).subscribe({
      next: (user) => {
        this.usuario.set(user);
      },
      error: () => {
        this.usuario.set(null);
      },
    });
  }

  get nombreCompleto(): string {
    const user = this.usuario();
    if (user) {
      return `${user.firstName} ${user.surnames}`.trim();
    }
    return 'Usuario';
  }

  get inicial(): string {
    const user = this.usuario();
    if (user) {
      return user.firstName.charAt(0).toUpperCase();
    }
    return 'U';
  }
}
