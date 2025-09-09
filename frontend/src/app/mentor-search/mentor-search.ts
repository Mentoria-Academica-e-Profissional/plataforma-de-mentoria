import { Component, ElementRef, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProfileService } from '../profile/profile.service';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { InterestArea, InterestAreaLabels } from '../material/material-module';
import { Mentored } from '../entity/mentored';

@Component({
  selector: 'app-mentor-search',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './mentor-search.html',
  styleUrl: './mentor-search.css'
})
export class MentorSearchComponent {
  mentoreds: Mentored[] = [];
  mentoredSearchPerformed = false;
  isInterestMenuOpen = false;
  
  selectedInterestAreas: string[] = [];
  interestAreaLabels: { [key: string]: string } = InterestAreaLabels;

  interestAreas = Object.keys(InterestArea).map(key => ({
    value: key,
    label: InterestAreaLabels[key as InterestArea]
  }));

  constructor(private profileService: ProfileService, private elementRef: ElementRef) {}
  
  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent) {
    if (!this.elementRef.nativeElement.querySelector('.dropdown-container')?.contains(event.target)) {
      this.isInterestMenuOpen = false;
    }
  }

  toggleInterestMenu(event: MouseEvent) {
    event.stopPropagation();
    this.isInterestMenuOpen = !this.isInterestMenuOpen;
  }

  onSearch(): void {
    this.mentoredSearchPerformed = true;

    if (this.selectedInterestAreas.length === 0) {
        this.mentoreds = [];
        return;
    }

    const searchObservables = this.selectedInterestAreas.map(area => 
        this.profileService.searchMentoreds(area).pipe(
            catchError(err => {
                console.error(`Erro ao buscar mentorados para a área ${area}:`, err);
                return of([]);
            })
        )
    );

    forkJoin(searchObservables).subscribe(resultsArrays => {
        const combinedResults = resultsArrays.flat();
        const uniqueMentoreds = Array.from(new Map(combinedResults.map(m => [m.id, m])).values());
        this.mentoreds = uniqueMentoreds;
    });
  }

  toggleAreaSelection(area: string): void {
    const index = this.selectedInterestAreas.indexOf(area);
    if (index > -1) {
      this.selectedInterestAreas.splice(index, 1);
    } else {
      this.selectedInterestAreas.push(area);
    }
    this.onSearch();
  }

  isAreaSelected(area: string): boolean {
    return this.selectedInterestAreas.includes(area);
  }

  clearInterestAreas(): void {
    this.selectedInterestAreas = [];
    this.onSearch();
  }

  getInitials(name: string): string {
    if (!name) return '';
    const names = name.trim().split(' ');
    const firstInitial = names[0]?.[0] || '';
    const lastInitial = names.length > 1 ? names[names.length - 1]?.[0] : '';
    return (firstInitial + lastInitial).toUpperCase();
  }

  private courseMap: { [key: string]: string } = {
      'ADMINISTRACAO': 'Administração', 'DIREITO': 'Direito', 'MEDICINA': 'Medicina', 'ENGENHARIA_CIVIL': 'Engenharia Civil',
      'CIENCIA_DA_COMPUTACAO': 'Ciência da Computação', 'PSICOLOGIA': 'Psicologia', 'ENFERMAGEM': 'Enfermagem',
      'ARQUITETURA_E_URBANISMO': 'Arquitetura e Urbanismo', 'CONTABILIDADE': 'Ciências Contábeis', 'ODONTOLOGIA': 'Odontologia',
      'PEDAGOGIA': 'Pedagogia', 'FISIOTERAPIA': 'Fisioterapia', 'NUTRICIONISMO': 'Nutrição', 'EDUCACAO_FISICA': 'Educação Física',
      'VETERINARIA': 'Medicina Veterinária', 'ZOOTECNIA': 'Zootecnia', 'LETRAS': 'Letras'
  };

  getCourseName(courseKey: string): string {
    return this.courseMap[courseKey] || courseKey;
  }
}