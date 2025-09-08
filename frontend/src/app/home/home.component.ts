import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Component, inject, OnInit } from '@angular/core';
import { AuthService } from '../auth/auth.service';
import { Router } from '@angular/router';
import { MentoredSearchComponent } from '../mentored-search/mentored-search';
import { MentorSearchComponent } from '../mentor-search/mentor-search';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterLink, MentoredSearchComponent, MentorSearchComponent],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css',
})
export class HomeComponent implements OnInit {
  title = 'Mentoria';
  results: { name: string; course: string; interests: string[] }[] = [];

  private authService = inject(AuthService);
  private router = inject(Router);

  ngOnInit() {

  }
  navigateToHistory(): void {
    this.router.navigate(['/sessions/history']);
  }
  
  navigateToMaterials(): void {
    this.router.navigate(['/materials']);
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  session() {
    this.router.navigate(['/sessions']);
  }

  isAuthenticated(): boolean {
    return this.authService.isAuthenticated();
  }

  hasMentorRole(): boolean {
    return this.authService.hasRole('MENTOR');
  }

  hasMentoredRole(): boolean {
    return this.authService.hasRole('MENTORADO');
  }
}
