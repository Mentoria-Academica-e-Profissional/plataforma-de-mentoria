import { Injectable } from '@angular/core';
import { User } from '../entity/user';
import { jwtDecode } from 'jwt-decode';
import { Mentor } from '../entity/mentor';
import { Mentored } from '../entity/mentored';
import { responseMentored } from '../entity/responses/response-mentored';
import { responseMentor } from '../entity/responses/response-mentor';
import { MentorDTO } from '../entity/dto/mentor-dto';
import { environment } from '../../../environments/environment';
@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private apiUrl = environment.apiUrl + "/auth";
  private apiUrlMentor = environment.apiUrl + "/mentor";
  private apiUrlMentored = environment.apiUrl + "/mentored";

  async login(email: string, password: string): Promise<boolean> {
    try {
      const response = await fetch(this.apiUrl + '/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password }),
      });

      if (!response.ok) {
        const errorData = await response
          .json()
          .catch(() => ({ message: 'Erro desconhecido' }));
        const error = new Error(errorData.message || 'Login falhou');
        (error as any).status = response.status;
        throw error;
      }

      const data = await response.json();
      localStorage.setItem('token', data.token);
      return true;
    } catch (error) {
      console.error('Erro ao fazer login no AuthService:', error);
      throw error;
    }
  }

  async register(newUser: User): Promise<User | null> {
    try {
      const response = await fetch(this.apiUrl + '/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(newUser),
      });

      if (!response.ok) {
        const errorData = await response
          .json()
          .catch(() => ({ message: 'Erro desconhecido' }));
        const error = new Error(errorData.message || 'Registro falhou');
        (error as any).status = response.status;
        throw error;
      }

      return await response.json();
    } catch (error) {
      console.error('Erro ao registrar usuário no AuthService:', error);
      throw error;
    }
  }

   async getCurrentUser(): Promise<User | null> {
    try {
      const response = await fetch(`${this.apiUrl}/user`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${this.getToken()}`,
        },
      });

      if (!response.ok) {
        const errorText = await response.text();
        console.error('Erro na resposta:', errorText);
        throw new Error(`Erro ao buscar perfil do usuario: ${response.status}`);
      }

      const user = await response.json();
      return user;
    } catch (error) {
      console.error('Erro ao buscar perfil do usuario:', error);
      throw error;
    }
  }

  async registerMentor(newMentor: Mentor): Promise<Mentor | null> {
    try {
      const response = await fetch(this.apiUrlMentor, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${localStorage.getItem('token')}`,
        },
        body: JSON.stringify(newMentor),
      });

      if (!response.ok) {
        const errorData = await response
          .json()
          .catch(() => ({ message: 'Erro desconhecido' }));
        const error = new Error(errorData.message || 'Registro falhou');
        (error as any).status = response.status;
        throw error;
      }

      return await response.json();
    } catch (error) {
      console.error('Erro ao registrar Mentor no AuthService:', error);
      throw error;
    }
  }

  async getAllMentor(): Promise<MentorDTO[] | null> {
    try {
      const response = await fetch(this.apiUrlMentor, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${localStorage.getItem('token')}`,
        }
      });

      if (!response.ok) {
        const errorData = await response
          .json()
          .catch(() => ({ message: 'Erro desconhecido' }));
        const error = new Error(errorData.message || 'Buscar mentores falhou');
        (error as any).status = response.status;
        throw error;
      }

      return await response.json();
    } catch (error) {
      console.error('Erro ao Buscar mentores no AuthService:', error);
      throw error;
    }
  }

  async registerMentored(newMentored: Mentored): Promise<Mentor | null> {
    try {
      const response = await fetch(this.apiUrlMentored, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${localStorage.getItem('token')}`,
        },
        body: JSON.stringify(newMentored),
      });

      if (!response.ok) {
        const errorData = await response
          .json()
          .catch(() => ({ message: 'Erro desconhecido' }));
        const error = new Error(errorData.message || 'Registro falhou');
        (error as any).status = response.status;
        throw error;
      }

      return await response.json();
    } catch (error) {
      console.error('Erro ao registrar Mentored no AuthService:', error);
      throw error;
    }
  }

  async getAllMentored(): Promise<responseMentored[] | null> {
    try {
      const response = await fetch(this.apiUrlMentored, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${localStorage.getItem('token')}`,
        }
      });

      if (!response.ok) {
        const errorData = await response
          .json()
          .catch(() => ({ message: 'Erro desconhecido' }));
        const error = new Error(errorData.message || 'Buscar mentorados falhou');
        (error as any).status = response.status;
        throw error;
      }

      return await response.json();
    } catch (error) {
      console.error('Erro ao Buscar mentorados no AuthService:', error);
      throw error;
    }
  }

  async deleteMentor(id: number): Promise<void> {
    try {
      const response = await fetch(`${this.apiUrlMentor}/${id}`, {
        method: 'DELETE',
        headers: {
          'Authorization': `Bearer ${this.getToken()}`,
        },
      });

      if (!response.ok) {
        const errorData = await response
          .json()
          .catch(() => ({ message: 'Erro desconhecido' }));
        const error = new Error(errorData.message || 'Erro ao deletar Mentor');
        (error as any).status = response.status;
        throw error;
      }
    } catch (error) {
      console.error('Erro ao deletar Mentor no AuthService:', error);
      throw error;
    }
  }

    async updateMentored(id: number, mentored: Mentored): Promise<Mentored | null> {
    try {
      const response = await fetch(`${this.apiUrlMentored}/${id}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${this.getToken()}`,
        },
        body: JSON.stringify(mentored),
      });

      if (!response.ok) {
        const errorData = await response
          .json()
          .catch(() => ({ message: 'Erro desconhecido' }));
        const error = new Error(errorData.message || 'Atualização falhou');
        (error as any).status = response.status;
        throw error;
      }

      return await response.json();
    } catch (error) {
      console.error('Erro ao atualizar Mentored no AuthService:', error);
      throw error;
    }
  }


    async deleteMentored(id: number): Promise<void> {
    try {
      const response = await fetch(`${this.apiUrlMentored}/${id}`, {
        method: 'DELETE',
        headers: {
          'Authorization': `Bearer ${this.getToken()}`,
        },
      });

      if (!response.ok) {
        const errorData = await response
          .json()
          .catch(() => ({ message: 'Erro desconhecido' }));
        const error = new Error(errorData.message || 'Erro ao deletar Mentored');
        (error as any).status = response.status;
        throw error;
      }
    } catch (error) {
      console.error('Erro ao deletar Mentored no AuthService:', error);
      throw error;
    }
  }

 

  logout(): void {
    localStorage.removeItem('token');
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
      return decodedToken.role?.includes(role) || false;
    } catch (error) {
      console.error('Erro ao decodificar o token:', error);
      return false;
    }
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }
}