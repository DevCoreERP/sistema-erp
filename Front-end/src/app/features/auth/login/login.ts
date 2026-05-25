import { Component, inject } from '@angular/core';
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

  form = new FormGroup({
    email: new FormControl('', [Validators.required, Validators.email]),
    password: new FormControl('', [Validators.required]),
  });

  submitted = false;
  errorMessage = '';

  onSubmit() {
    this.submitted = true;
    this.errorMessage = '';

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const { email, password } = this.form.value;

    this.authService.login({ email: email!, password: password! }).subscribe({
      next: () => {
        this.router.navigate(['/panel']);
      },
      error: (err) => {
        this.errorMessage = err.status === 401
          ? 'Credenciales inválidas. Verifica tu correo y contraseña.'
          : 'Error al iniciar sesión. Intenta de nuevo.';
      },
    });
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
