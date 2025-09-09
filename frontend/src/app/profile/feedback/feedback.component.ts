import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ProfileService } from '../profile.service';
import { Feedback } from '../../entity/Feedback';

@Component({
  selector: 'app-feedback',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './feedback.component.html',
  styleUrls: ['./feedback.component.css']
})
export class FeedbackComponent implements OnInit {
  feedbacks: Feedback[] = [];
  loading = true;
  error: string | null = null;

  private router = inject(Router);
  private profileService = inject(ProfileService);

  ngOnInit(): void {
    this.loadFeedbacks();
  }

  loadFeedbacks(): void {
    this.loading = true;
    this.error = null;

    this.profileService.getFeedbacks().subscribe({
      next: (data) => {
        this.feedbacks = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Erro ao carregar avaliações';
        this.loading = false;
        console.error('Error loading feedbacks:', err);
      }
    });
  }

  getStars(nota: number): string[] {
    return Array(5).fill('').map((_, i) => i < nota ? 'full' : 'empty');
  }

  formatDate(dateStr: string): string {
    const date = new Date(dateStr);
    return date.toLocaleDateString('pt-BR');
  }

  goBack(): void {
    this.router.navigate(['/profile']);
  }
}
