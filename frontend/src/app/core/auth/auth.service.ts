import { Injectable, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';

import { AuthResponse, LoginRequest, VerifyOtpRequest } from './auth.models';
import { environment } from '../../../environments/environment';
import { Observable, tap, throwError } from 'rxjs';
const ACCESS_TOKEN_KEY = 'ops_access_token';
const REFRESH_TOKEN_KEY = 'ops_refresh_token';
const USER_KEY = 'ops_user';

@Injectable({ providedIn: 'root' })
export class AuthService {

  // signal réactif utilisé dans toute l'app (sidebar, guards, etc.)
  private currentUserSignal = signal<Partial<AuthResponse> | null>(this.loadStoredUser());

  readonly currentUser = computed(() => this.currentUserSignal());
  readonly isAuthenticated = computed(() => !!this.currentUserSignal());
  readonly role = computed(() => this.currentUserSignal()?.role ?? null);
  readonly region = computed(() => this.currentUserSignal()?.region ?? null);

  constructor(private http: HttpClient, private router: Router) {}

  login(payload: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${environment.apiUrl}/auth/login`, payload).pipe(
      tap(response => {
        if (!response.otpRequired) {
          this.persistSession(response);
        }
      })
    );
  }

  verifyOtp(payload: VerifyOtpRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${environment.apiUrl}/auth/verify-otp`, payload).pipe(
      tap(response => this.persistSession(response))
    );
  }
  refreshAccessToken(): Observable<AuthResponse> {
    const refreshToken = localStorage.getItem('ops_refresh_token');
    if (!refreshToken) {
      return throwError(() => new Error('Aucun refresh token disponible'));
    }
    return this.http.post<AuthResponse>(`${environment.apiUrl}/auth/refresh`, { refreshToken }).pipe(
      tap(response => this.persistSession(response))
    );
  }

  logout(): void {
    localStorage.removeItem(ACCESS_TOKEN_KEY);
    localStorage.removeItem(REFRESH_TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    this.currentUserSignal.set(null);
    this.router.navigate(['/login']);
  }

  getAccessToken(): string | null {
    return localStorage.getItem(ACCESS_TOKEN_KEY);
  }

  hasPermission(permissionCode: string): boolean {
    // Les permissions détaillées peuvent aussi être décodées depuis le JWT si besoin.
    // Ici on s'appuie sur le rôle pour une vérification simple côté UI
    // (l'application stricte reste toujours faite côté backend).
    const role = this.role();
    if (role === 'SUPER_ADMIN') return true;
    return !!role;
  }

  private persistSession(response: AuthResponse): void {
    if (response.accessToken) localStorage.setItem(ACCESS_TOKEN_KEY, response.accessToken);
    if (response.refreshToken) localStorage.setItem(REFRESH_TOKEN_KEY, response.refreshToken);
    localStorage.setItem(USER_KEY, JSON.stringify(response));
    this.currentUserSignal.set(response);
  }

  private loadStoredUser(): Partial<AuthResponse> | null {
    const raw = localStorage.getItem(USER_KEY);
    return raw ? JSON.parse(raw) : null;
  }
}
