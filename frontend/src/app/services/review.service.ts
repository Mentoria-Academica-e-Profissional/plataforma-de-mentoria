import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { Feedback } from '../entity/Feedback';

export interface ReviewData {
  score: number;
  comment: string;
  sessionId: number;
  reviewerRole: string;
}

export interface ReviewResponse {
  id: number;
  score: number;
  comment: string;
  sessionId: number;
  reviewerRole: string;
  createdAt: string;
  mentorId: number;
  mentoredId: number;
}

export interface ReviewResponse {
  id: number;
  score: number;
  comment: string;
  sessionId: number;
  reviewerRole: string;
  createdAt: string;
}

@Injectable({
  providedIn: 'root'
})
export class ReviewService {
  private apiUrl = `${environment.apiUrl}/avaliacoes`;

  constructor(private http: HttpClient) {}

  /**
   * Submit a new review for a session
   */
  submitReview(reviewData: ReviewData): Promise<ReviewResponse> {
    const token = this.getToken();
    
    return fetch(this.apiUrl, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify(reviewData)
    }).then(async (response) => {
      if (!response.ok) {
        const responseText = await response.text();
        let errorData;
        try {
          errorData = JSON.parse(responseText);
        } catch (e) {
          errorData = { message: responseText || 'Erro desconhecido' };
        }
        
        // Handle specific error cases
        if (response.status === 409) {
          throw new Error('Você já avaliou esta sessão. Cada sessão pode ser avaliada apenas uma vez.');
        } else if (response.status === 400) {
          throw new Error(errorData.message || 'Dados da avaliação inválidos. Verifique os campos e tente novamente.');
        } else if (response.status === 403) {
          throw new Error('Você não tem permissão para avaliar esta sessão.');
        } else {
          throw new Error(errorData.message || `Erro ${response.status}: ${response.statusText}`);
        }
      }
      
      return response.json();
    });
  }

  /**
   * Get reviews that the current user has received
   */
  getReceivedReviews(): Observable<Feedback[]> {
    const token = this.getToken();
    const headers = new HttpHeaders({ 
      Authorization: `Bearer ${token}` 
    });

    return this.http.get<any[]>(`${this.apiUrl}/minhas`, { headers }).pipe(
      catchError((error: any) => {
        console.error('Erro ao carregar avaliações recebidas:', error);
        return of([]);
      })
    );
  }

  /**
   * Get reviews that the current user has created
   */
  getCreatedReviews(): Observable<ReviewResponse[]> {
    const token = this.getToken();
    const headers = new HttpHeaders({ 
      Authorization: `Bearer ${token}` 
    });

    return this.http.get<ReviewResponse[]>(`${this.apiUrl}/criadas`, { headers }).pipe(
      catchError((error: any) => {
        console.error('Erro ao carregar avaliações criadas:', error);
        return of([]);
      })
    );
  }

  /**
   * Check if a session has already been reviewed by the current user
   */
  hasUserReviewedSession(sessionId: number): Observable<boolean> {
    return new Observable((observer) => {
      this.getCreatedReviews().subscribe({
        next: (reviews) => {
          const hasReviewed = reviews.some(review => review.sessionId === sessionId);
          observer.next(hasReviewed);
          observer.complete();
        },
        error: (error) => {
          console.error('Erro ao verificar se sessão foi avaliada:', error);
          observer.next(false); // Default to false if there's an error
          observer.complete();
        }
      });
    });
  }

  /**
   * Get all reviewed session IDs for the current user
   */
  getReviewedSessionIds(): Observable<number[]> {
    return new Observable((observer) => {
      this.getCreatedReviews().subscribe({
        next: (reviews) => {
          const sessionIds = reviews.map(review => review.sessionId);
          observer.next(sessionIds);
          observer.complete();
        },
        error: (error) => {
          console.error('Erro ao carregar IDs de sessões avaliadas:', error);
          observer.next([]); // Return empty array if there's an error
          observer.complete();
        }
      });
    });
  }

  private getToken(): string | null {
    return localStorage.getItem('token');
  }
}
