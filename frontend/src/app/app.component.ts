import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { RouterModule, Router, NavigationEnd } from '@angular/router';
import { AuthService } from './auth/auth.service';
import { filter } from 'rxjs/operators';

@Component({
  standalone: true,
  selector: 'app-root',
  imports: [RouterModule, CommonModule],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent implements OnInit {
  title = 'Mentoria';
  isMenuOpen = false;
  showLayout = false;

  private authService = inject(AuthService);
  private router = inject(Router);

  ngOnInit() {
    this.router.events.pipe(
      filter((event): event is NavigationEnd => event instanceof NavigationEnd)
    ).subscribe((event: NavigationEnd) => {
      if (this.authService.isAuthenticated()) {
        const layoutRoutes = ['/home', '/profile', '/sessions', '/materials', '/sessions/history'];
        this.showLayout = layoutRoutes.some(route => event.urlAfterRedirects.startsWith(route));
      } else {
        this.showLayout = false;
      }
    });
  }

  toggleMenu() {
    this.isMenuOpen = !this.isMenuOpen;
  }

  navigateTo(path: string) {
    this.router.navigate([path]);
    this.isMenuOpen = false;
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
    this.isMenuOpen = false;
  }

  isAuthenticated(): boolean {
    return this.authService.isAuthenticated();
  }
}