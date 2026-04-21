import { Component, inject } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './login.html',
})
export class Login {
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
    // TODO: lógica de autenticación
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
