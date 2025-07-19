import { Injectable } from '@angular/core';
import { jwtDecode } from 'jwt-decode';

export interface User {
  email: string;
  password: string;
  [key: string]: any;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/auth';

  async login(email: string, password: string): Promise<boolean> {
    try {
      const response = await fetch(this.apiUrl, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password }),
      });

      if (!response.ok) {
        throw new Error('Login falhou');
      }

      const data = await response.json();
      localStorage.setItem('token', data.token);
      return true;
    } catch (error) {
      console.error('Erro ao fazer login:', error);
      return false;
    }
  }

  logout(): void {
    localStorage.removeItem('token');
  }

  async register(newUser: User): Promise<User | null> {
    try {
      const response = await fetch(this.apiUrl, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(newUser)
      });
      return await response.json();
    } catch (error) {
      console.error('Erro ao registrar usuário:', error);
      return null;
    }
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  hasRole(role: string): boolean {
    const token = this.getToken();
    if (!token) {
      return false;
    }
    try {
      const decodedToken: any = jwtDecode(token);
      return decodedToken.roles?.includes(role) || false;
    } catch (error) {
      console.error('Erro ao decodificar o token:', error);
      return false;
    }
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }
}
