export interface LoginRequest {
  email: string;
  password: string;
}

export interface VerifyOtpRequest {
  email: string;
  code: string;
}

export interface AuthResponse {
  accessToken: string | null;
  refreshToken: string | null;
  otpRequired: boolean;
  fullName: string | null;
  role: string | null;
  region: string | null;
}
