import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../auth/auth.service';
import { MentoredSearchComponent } from '../mentored-search/mentored-search';
import { MentorSearchComponent } from '../mentor-search/mentor-search';
import { Material, InterestArea, InterestAreaLabels } from '../material/material-module';
import { MaterialService } from '../material/material.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, MentoredSearchComponent, MentorSearchComponent],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
})
export class HomeComponent implements OnInit {
  private authService = inject(AuthService);
  private materialService = inject(MaterialService);
  
  userRole: string | null = null;
  suggestedMaterials: Material[] = [];
  filteredMaterials: Material[] = [];
  isLoading = false;
  showFilter = false;
  
  interestAreas = Object.values(InterestArea);
  interestAreaLabels = InterestAreaLabels;
  selectedInterestAreas: InterestArea[] = [];

  ngOnInit() {
    this.userRole = this.authService.getRole();
    this.loadSuggestedMaterials();
  }

  loadSuggestedMaterials(): void {
    this.isLoading = true;
    this.materialService.getSuggestedMaterials().subscribe({
      next: (materials) => {
        this.suggestedMaterials = materials;
        this.applyFilters();
        this.isLoading = false;
      },
      error: () => this.isLoading = false
    });
  }

  toggleFilter(): void {
    this.showFilter = !this.showFilter;
  }

  toggleAreaSelection(area: InterestArea): void {
    const index = this.selectedInterestAreas.indexOf(area);
    if (index > -1) {
      this.selectedInterestAreas.splice(index, 1);
    } else {
      this.selectedInterestAreas.push(area);
    }
    this.applyFilters();
  }

  isAreaSelected(area: InterestArea): boolean {
    return this.selectedInterestAreas.includes(area);
  }

  clearFilters(): void {
    this.selectedInterestAreas = [];
    this.applyFilters();
  }
  
  applyFilters(): void {
    if (this.selectedInterestAreas.length === 0) {
      this.filteredMaterials = this.suggestedMaterials;
      return;
    }
    this.filteredMaterials = this.suggestedMaterials.filter(material =>
      material.interestArea.some(area => this.selectedInterestAreas.includes(area))
    );
  }
  
  getMaterialTypeLabel(type: string): string {
    switch (type) {
      case 'DOCUMENTO': return 'Documento';
      case 'VIDEO': return 'Vídeo';
      case 'LINK': return 'Link';
      default: return type;
    }
  }

  formatDate(date: any): string {
    if (!date) return '';
    return new Date(date).toLocaleDateString('pt-BR');
  }

  hasMentorRole(): boolean {
    return this.authService.hasRole('MENTOR');
  }

  hasMentoredRole(): boolean {
    return this.authService.hasRole('MENTORADO');
  }
}