import { Component, inject } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: true, 
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './login.html',
})
export class Login {
  private router = inject(Router);

  form = new FormGroup({
    email: new FormControl('', [Validators.required, Validators.email]),
    password: new FormControl('', [Validators.required]),
  });

  submitted = false;

  onSubmit() {
  this.submitted = true;
  if (this.form.invalid) {
    this.form.markAllAsTouched();
    return;
  }

  const usuario = {
    nombre: 'Juan',
    apellido: 'Pérez',
    email: this.form.value.email ?? ''
  };

  localStorage.setItem('usuario', JSON.stringify(usuario));

  this.router.navigate(['/panel']);
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