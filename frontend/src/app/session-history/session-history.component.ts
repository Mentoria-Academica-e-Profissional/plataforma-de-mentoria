import { Component, OnInit, inject } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ProfileService } from '../profile/profile.service';
import { AuthService } from '../auth/auth.service';
import { ReviewService, ReviewData } from '../services/review.service';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-session-history',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './session-history.component.html',
  styleUrls: ['./session-history.component.css'],
  providers: [DatePipe],
})
export class SessionHistoryComponent implements OnInit {
  sessions: any[] = [];
  filteredSessions: any[] = [];
  reviewedSessions: Set<number> = new Set(); // Track reviewed sessions
  expandedSessionId: number | null = null;
  reviewForm: FormGroup;
  statusUpdateForm: FormGroup;
  currentUser: any = null;
  isSubmittingReview = false;
  isUpdatingStatus = false;
  errorMessage: string = '';
  successMessage: string = '';

  statusFilters = [
    { value: 'PENDING', label: 'Pendente', checked: true },
    { value: 'ACCEPTED', label: 'Aceito', checked: true },
    { value: 'REJECTED', label: 'Rejeitado', checked: true },
    { value: 'COMPLETED', label: 'Concluído', checked: true },
    { value: 'CANCELLED', label: 'Cancelado', checked: true },
  ];

  availableStatusOptions = [
    { value: 'ACCEPTED', label: 'Aceito' },
    { value: 'REJECTED', label: 'Rejeitado' },
    { value: 'COMPLETED', label: 'Concluído' },
    { value: 'CANCELLED', label: 'Cancelado' },
  ];

  getAvailableStatusOptions(currentStatus: string): any[] {
    switch (currentStatus) {
      case 'PENDING':
        return [
          { value: 'ACCEPTED', label: 'Aceito' },
          { value: 'REJECTED', label: 'Rejeitado' },
          { value: 'CANCELLED', label: 'Cancelado' }
        ];
      case 'ACCEPTED':
        return [
          { value: 'COMPLETED', label: 'Concluído' },
          { value: 'CANCELLED', label: 'Cancelado' }
        ];
      default:
        return []; // No transitions available for COMPLETED, REJECTED, CANCELLED
    }
  }

  private profileService = inject(ProfileService);
  private authService = inject(AuthService);
  private router = inject(Router);
  private fb = inject(FormBuilder);
  private reviewService = inject(ReviewService);

  constructor() {
    this.reviewForm = this.fb.group({
      score: [5, [Validators.required, Validators.min(1), Validators.max(5)]],
      comment: ['', [Validators.maxLength(600)]]
    });

    this.statusUpdateForm = this.fb.group({
      newStatus: ['', [Validators.required]]
    });
  }

  async ngOnInit(): Promise<void> {
    this.currentUser = await this.authService.getCurrentUser();
    this.loadSessionHistory();
    this.loadReviewedSessions();
  }

  private loadReviewedSessions(): void {
    this.reviewService.getReviewedSessionIds().subscribe({
      next: (reviewedIds) => {
        this.reviewedSessions = new Set(reviewedIds);
      },
      error: (error) => {
        console.error('Error loading reviewed sessions:', error);
        // Continue without reviewed sessions data
      }
    });
  }

  navigateTo(path: string): void {
    this.router.navigate([path]);
  }

  loadSessionHistory(): void {
    this.profileService.getSessionHistory().subscribe({
      next: (data) => {
        this.sessions = data;
        this.applyFilters();
      },
      error: (err) => {
        console.error('Erro ao carregar histórico de sessões:', err);
      },
    });
  }

  onFilterChange(status: string): void {
    const filter = this.statusFilters.find((f) => f.value === status);
    if (filter) {
      filter.checked = !filter.checked;
      this.applyFilters();
    }
  }

  applyFilters(): void {
    const selectedStatuses = this.statusFilters
      .filter((filter) => filter.checked)
      .map((filter) => filter.value);

    this.filteredSessions = this.sessions.filter((session) =>
      selectedStatuses.includes(session.status)
    );
  }

  toggleAllFilters(selectAll: boolean): void {
    this.statusFilters.forEach((filter) => (filter.checked = selectAll));
    this.applyFilters();
  }

  get allFiltersSelected(): boolean {
    return this.statusFilters.every((filter) => filter.checked);
  }

  get noFiltersSelected(): boolean {
    return this.statusFilters.every((filter) => !filter.checked);
  }

  getStatusClass(status: string): string {
    return `status-${status.toLowerCase()}`;
  }

  getStatusLabel(status: string): string {
    const filter = this.statusFilters.find((f) => f.value === status);
    return filter ? filter.label : status;
  }

  goBack(): void {
    this.router.navigate(['/home']);
  }

  toggleSessionExpansion(sessionId: number): void {
    console.log('Toggling session:', sessionId, 'Current expanded:', this.expandedSessionId);
    if (this.expandedSessionId === sessionId) {
      this.expandedSessionId = null;
    } else {
      this.expandedSessionId = sessionId;
      this.resetReviewForm();
    }
    console.log('New expanded session:', this.expandedSessionId);
  }

  isSessionExpanded(sessionId: number): boolean {
    const isExpanded = this.expandedSessionId === sessionId;
    console.log('Is session expanded?', sessionId, isExpanded);
    return isExpanded;
  }

  canReview(session: any): boolean {
    const isCompleted = session.status === 'COMPLETED';
    const notYetReviewed = !this.reviewedSessions.has(session.id);
    console.log('Session status:', session.status, 'Is completed:', isCompleted, 'Not yet reviewed:', notYetReviewed);
    return isCompleted && notYetReviewed;
  }

  isSessionReviewed(session: any): boolean {
    return this.reviewedSessions.has(session.id);
  }

  canUpdateStatus(session: any): boolean {
    return this.currentUser?.role === 'MENTOR' && 
           (session.status === 'PENDING' || session.status === 'ACCEPTED');
  }

  canExpand(session: any): boolean {
    return this.canReview(session) || this.canUpdateStatus(session);
  }

  getSessionHint(session: any): string {
    if (this.currentUser?.role === 'MENTOR') {
      if (session.status === 'PENDING' || session.status === 'ACCEPTED') {
        return 'Clique para gerenciar status';
      } else {
        return 'Status não pode ser alterado';
      }
    } else {
      if (session.status === 'COMPLETED') {
        return 'Clique para avaliar';
      } else {
        return 'Avaliação disponível após conclusão';
      }
    }
  }

  resetReviewForm(): void {
    this.reviewForm.reset({
      score: 5,
      comment: ''
    });
    this.statusUpdateForm.reset({
      newStatus: ''
    });
  }

  clearMessages(): void {
    this.errorMessage = '';
    this.successMessage = '';
  }

  private showSuccessMessage(message: string): void {
    this.successMessage = message;
    this.errorMessage = '';
    setTimeout(() => {
      this.successMessage = '';
    }, 5000);
  }

  private showErrorMessage(message: string): void {
    this.errorMessage = message;
    this.successMessage = '';
  }

  hasUserReviewedSession(sessionId: number): boolean {
    return this.reviewedSessions.has(sessionId);
  }

  setRating(rating: number): void {
    this.reviewForm.patchValue({ score: rating });
  }

  getStarArray(): number[] {
    return [1, 2, 3, 4, 5];
  }

  async submitReview(session: any): Promise<void> {
    if (this.reviewForm.valid && this.currentUser) {
      this.isSubmittingReview = true;
      this.clearMessages();

      try {
        const reviewData: ReviewData = {
          score: this.reviewForm.value.score,
          comment: this.reviewForm.value.comment || '',
          sessionId: session.id,
          reviewerRole: this.currentUser.role
        };

        await this.reviewService.submitReview(reviewData);
        
        // Add session to reviewed set
        this.reviewedSessions.add(session.id);
        
        this.showSuccessMessage('Avaliação enviada com sucesso!');
        this.expandedSessionId = null;
        this.resetReviewForm();
        
      } catch (error: any) {
        console.error('Erro ao enviar avaliação:', error);
        this.showErrorMessage(error.message || 'Erro ao enviar avaliação. Tente novamente.');
      } finally {
        this.isSubmittingReview = false;
      }
    }
  }

  async updateSessionStatus(session: any): Promise<void> {
    if (this.statusUpdateForm.valid && this.currentUser) {
      this.isUpdatingStatus = true;

      try {
        const newStatus = this.statusUpdateForm.value.newStatus;
        const sessionId = session.id;
        
        if (!sessionId) {
          throw new Error('ID da sessão não encontrado');
        }
        
        const response = await fetch(`${environment.apiUrl}/sessions/${sessionId}/status?newStatus=${newStatus}`, {
          method: 'PATCH',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${this.authService.getToken()}`
          }
        });

        console.log('Response status:', response.status);
        console.log('Response headers:', response.headers);

        if (!response.ok) {
          const responseText = await response.text();
          console.log('Error response text:', responseText);
          let errorData;
          try {
            errorData = JSON.parse(responseText);
          } catch (e) {
            errorData = { message: responseText || 'Erro desconhecido' };
          }
          throw new Error(errorData.message || `Erro ${response.status}: ${response.statusText}`);
        }

        alert('Status da sessão atualizado com sucesso!');
        this.expandedSessionId = null;
        this.resetReviewForm();
        this.loadSessionHistory(); // Reload to show updated status
        
      } catch (error: any) {
        console.error('Erro ao atualizar status:', error);
        alert(error.message || 'Erro ao atualizar status da sessão. Tente novamente.');
      } finally {
        this.isUpdatingStatus = false;
      }
    }
  }
}
