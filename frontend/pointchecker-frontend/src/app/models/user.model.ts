export interface User {
  username: string;
}

export interface AuthResponse {
  id: number;
  username: string;
  sessionToken: string;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  password: string;
}
