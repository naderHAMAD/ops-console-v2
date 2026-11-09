import { Component, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/auth/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login.component.html'
})
export class LoginComponent {

  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);

  otpMode = signal(false);
  errorMessage = signal<string | null>(null);
  loading = signal(false);

  loginForm = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', Validators.required]
  });

  otpForm = this.fb.group({
    code: ['', [Validators.required, Validators.minLength(6), Validators.maxLength(6)]]
  });

  constructor() {}

  submitLogin(): void {
    if (this.loginForm.invalid) return;
    this.loading.set(true);
    this.errorMessage.set(null);

    const { email, password } = this.loginForm.value;

    this.authService.login({ email: email!, password: password! }).subscribe({
      next: (res) => {
        this.loading.set(false);
        if (res.otpRequired) {
          this.otpMode.set(true);
        } else {
          this.router.navigate(['/dashboard']);
        }
      },
      error: () => {
        this.loading.set(false);
        this.errorMessage.set('Email ou mot de passe incorrect.');
      }
    });
  }

  submitOtp(): void {
    if (this.otpForm.invalid) return;
    this.loading.set(true);
    this.errorMessage.set(null);

    const email = this.loginForm.value.email!;
    const code = this.otpForm.value.code!;

    this.authService.verifyOtp({ email, code }).subscribe({
      next: () => {
        this.loading.set(false);
        this.router.navigate(['/dashboard']);
      },
      error: () => {
        this.loading.set(false);
        this.errorMessage.set('Code de vérification invalide.');
      }
    });
  }
}
