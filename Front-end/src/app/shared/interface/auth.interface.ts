export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  message: string;
  usuario: AuthUser;
}

export interface UsuarioResponse {
  id: number;
  username: string;
  firstName: string;
  surnames: string;
  email: string;
  phoneNumber: string;
  roleName: string;
}

export interface AuthUser {
  id: number;
  username: string;
  firstName: string;
  surnames: string;
  email: string;
  phoneNumber: string;
  roleName: string;
}
