import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable, BehaviorSubject, of } from 'rxjs';
import { map, switchMap, catchError } from 'rxjs/operators';
import { Mentor } from '../entity/mentor';
import { environment } from '../../../environments/environment';
import { Feedback } from '../entity/Feedback';
import { ReviewService } from '../services/review.service';

export interface ProfileData {
  type: 'MENTOR' | 'MENTORADO' | 'UNKNOWN';
  data: any;
}
interface UserTokenPayload {
  id: number;
  role: 'MENTOR' | 'MENTORADO' | 'UNKNOWN';
}

@Injectable({
  providedIn: 'root'
})
export class ProfileService {
  private apiUrl = environment.apiUrl;
  private profileTypeSubject = new BehaviorSubject<string>('UNKNOWN');
  public profileType$ = this.profileTypeSubject.asObservable();

  constructor(private http: HttpClient, private reviewService: ReviewService) {}

  getSessionHistory(): Observable<any[]> {
     const userInfo = this.getUserInfoFromToken();

     if (!userInfo || userInfo.role === 'UNKNOWN') {
       console.error('Usuário não autenticado ou papel inválido.');
       return of([]);
     }

     let endpoint = '';
     if (userInfo.role === 'MENTOR') {
       endpoint = `/sessions/history/mentor/${userInfo.id}`;
     } else if (userInfo.role === 'MENTORADO') {
       endpoint = `/sessions/history/mentored/${userInfo.id}`;
     } else {
       return of([]);
     }

     const token = localStorage.getItem('token');
     const headers = new HttpHeaders({ Authorization: `Bearer ${token}` });

     return this.http.get<any[]>(`${this.apiUrl}${endpoint}`, { headers }).pipe(
       catchError((error: any) => {
         console.error('Erro ao buscar histórico de sessões:', error);
         return of([]);
       })
     );
   }

   private getUserInfoFromToken(): UserTokenPayload | null {
     const token = localStorage.getItem('token');
     if (!token) return null;

     try {
       const payload = JSON.parse(atob(token.split('.')[1]));
       return {
         id: payload.userId,
         role: payload.role || 'UNKNOWN'
       };
     } catch (error) {
       console.error('Erro ao decodificar token:', error);
       return null;
     }
   }

   getCompleteProfile(): Observable<ProfileData> {
     const userInfo = this.getUserInfoFromToken();
     const userRole = userInfo ? userInfo.role : 'UNKNOWN';
     this.profileTypeSubject.next(userRole);

     const token = localStorage.getItem('token');
     const headers = { Authorization: `Bearer ${token}` };

     if (userRole === 'MENTOR') {
       return this.http.get(`${this.apiUrl}/mentor/me`, { headers }).pipe(
         map((data: any) => ({ type: 'MENTOR' as const, data })),
         catchError((error: any) => {
           console.error('Erro ao buscar perfil do mentor:', error);
           return of({ type: 'UNKNOWN' as const, data: null });
         })
       );
     } else if (userRole === 'MENTORADO') {
       return this.http.get(`${this.apiUrl}/mentored/me`, { headers }).pipe(
         map((data: any) => ({ type: 'MENTORADO' as const, data })),
         catchError((error: any) => {
           console.error('Erro ao buscar perfil do mentorado:', error);
           return of({ type: 'UNKNOWN' as const, data: null });
         })
       );
     } else {
       return of({ type: 'UNKNOWN' as const, data: null });
     }
   }

 updateMentorProfile(idMentor: number, data: any): Observable<any> {
   const token = localStorage.getItem('token');
   const headers = { Authorization: `Bearer ${token}` };
   return this.http.put(`${this.apiUrl}/mentor/${idMentor}`, data, { headers });
 }

updateMentoredProfile(data: any): Observable<any> {
  const token = localStorage.getItem('token');
  const headers = { Authorization: `Bearer ${token}` };
  return this.http.get(`${this.apiUrl}/mentored/me`, { headers }).pipe(
    switchMap((currentMentored: any) => {
      return this.http.put(`${this.apiUrl}/mentored/${currentMentored.id}`, data, { headers });
    })
  );
}

  getCurrentProfileType(): string {
    return this.profileTypeSubject.value;
  }

  searchMentors(interestArea?: string, specializations?: string | string[]): Observable<Mentor[]> {
    let params = new HttpParams();
    if (interestArea) {
      params = params.set('interestArea', interestArea.toUpperCase());
    }
    if (specializations) {
      const specString = Array.isArray(specializations) ? specializations.join(',') : specializations;
      params = params.set('specializations', specString);
    }
    return this.http.get<Mentor[]>(`${this.apiUrl}/mentored/mentors/search`, { params });
  }

  searchMentoreds(interestArea?: string): Observable<any[]> {
    let params = new HttpParams();
    if (interestArea) {
      params = params.set('interestArea', interestArea.toUpperCase());
    }
    return this.http.get<any[]>(`${this.apiUrl}/mentor/mentoreds/search`, { params });
  }

  getFeedbacks(): Observable<Feedback[]> {
    return this.reviewService.getReceivedReviews().pipe(
      catchError((error: any) => {
        console.error('Erro ao carregar avaliações:', error);
        return of([]);
      })
    );
  }
}
