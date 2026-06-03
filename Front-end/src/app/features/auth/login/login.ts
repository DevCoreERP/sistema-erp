import { Component, inject, ChangeDetectorRef } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './login.html',
})
export class Login {
  private router = inject(Router);
  private authService = inject(AuthService);
  private cdr = inject(ChangeDetectorRef);

  showPassword = false;

  form = new FormGroup({
    subdomain: new FormControl('', [Validators.required]),
    email: new FormControl('', [Validators.required, Validators.email]),
    password: new FormControl('', [Validators.required]),
  });

  submitted = false;
  isLoading = false;
  errorMessage = '';

  onSubmit() {
    this.submitted = true;
    this.errorMessage = '';
    this.cdr.detectChanges();

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.isLoading = true;
    this.cdr.detectChanges();

    const { email, password, subdomain } = this.form.value;

    this.authService.login({ email: email!, password: password! }, subdomain!).subscribe({
      next: () => {
        this.isLoading = false;
        this.router.navigate(['/panel']);
      },
      error: (err) => {
        console.error('[LOGIN ERROR] Full backend response:', err);
        this.isLoading = false;
        // Try to extract the error message from the backend response
        const backendError = err.error?.error || err.error?.message;

        if (err.status === 0) {
          this.errorMessage = 'No se pudo conectar con el servidor. Verifica tu conexión o que el nombre de la Empresa sea correcto.';
        } else if (err.status === 401 || err.status === 403) {
          this.errorMessage = backendError || 'Credenciales inválidas. Verifica tu empresa, correo y contraseña.';
        } else if (err.status === 404) {
          this.errorMessage = backendError || 'Empresa no encontrada. Verifica el nombre de la empresa.';
        } else {
          this.errorMessage = backendError || 'Error al iniciar sesión. Intenta de nuevo.';
        }
        this.cdr.detectChanges();
      },
    });
  }

  togglePassword() {
    this.showPassword = !this.showPassword;
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.form.get(fieldName);
    if (!field) return false;
    const hasError = field.invalid && (field.touched || this.submitted);
    return hasError;
  }

  isFieldActive(fieldName: string): boolean {
    const field = this.form.get(fieldName);
    if (!field) return false;
    return document.activeElement === document.querySelector(`[formControlName="${fieldName}"]`);
  }
}
