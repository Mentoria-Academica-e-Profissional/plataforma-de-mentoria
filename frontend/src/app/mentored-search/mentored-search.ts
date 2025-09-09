import { Component, HostListener, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProfileService } from '../profile/profile.service';
import { Mentor } from '../entity/mentor';
import { of } from 'rxjs';
import { catchError } from 'rxjs/operators';

@Component({
  selector: 'app-mentored-search',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './mentored-search.html',
  styleUrl: './mentored-search.css'
})
export class MentoredSearchComponent {
  mentors: Mentor[] = [];
  mentorSearchPerformed = false;
  
  // --- UI State ---
  isInterestMenuOpen = false;
  private searchDebounceTimer: any;

  // --- Filtros ---
  selectedInterestAreas: string[] = [];
  specializationsInput: string = '';

  interestAreas = [
    { value: 'TECNOLOGIA_DA_INFORMACAO', label: 'Tecnologia da Informação' },
    { value: 'DESENVOLVIMENTO_DE_SOFTWARE', label: 'Desenvolvimento de Software' },
    { value: 'CIENCIA_DE_DADOS_E_IA', label: 'Ciência de Dados e Inteligência Artificial' },
    { value: 'CIBERSEGURANCA', label: 'Cibersegurança' },
    { value: 'UX_UI_DESIGN', label: 'UX/UI Design' },
    { value: 'ENGENHARIA_GERAL', label: 'Engenharia Geral' },
    { value: 'ENGENHARIA_CIVIL', label: 'Engenharia Civil' },
    { value: 'ENGENHARIA_DE_PRODUCAO', label: 'Engenharia de Produção' },
    { value: 'MATEMATICA_E_ESTATISTICA', label: 'Matemática e Estatística' },
    { value: 'FISICA', label: 'Física' },
    { value: 'ADMINISTRACAO_E_GESTAO', label: 'Administração e Gestão' },
    { value: 'EMPREENDEDORISMO_E_INOVACAO', label: 'Empreendedorismo e Inovação' },
    { value: 'FINANCAS_E_CONTABILIDADE', label: 'Finanças e Contabilidade' },
    { value: 'RECURSOS_HUMANOS', label: 'Recursos Humanos' },
    { value: 'LOGISTICA_E_CADEIA_DE_SUPRIMENTOS', label: 'Logística e Cadeia de Suprimentos' },
    { value: 'MARKETING_E_COMUNICACAO', label: 'Marketing e Comunicação' },
    { value: 'MARKETING_DIGITAL', label: 'Marketing Digital' },
    { value: 'JORNALISMO', label: 'Jornalismo' },
    { value: 'PUBLICIDADE_E_PROPAGANDA', label: 'Publicidade e Propaganda' },
    { value: 'COMUNICACAO_INSTITUCIONAL', label: 'Comunicação Institucional' },
    { value: 'CIENCIAS_BIOLOGICAS_E_SAUDE', label: 'Ciências Biológicas e Saúde' },
    { value: 'MEDICINA', label: 'Medicina' },
    { value: 'PSICOLOGIA', label: 'Psicologia' },
    { value: 'NUTRICAO', label: 'Nutrição' },
    { value: 'BIOTECNOLOGIA', label: 'Biotecnologia' },
    { value: 'EDUCACAO', label: 'Educação' },
    { value: 'CIENCIAS_HUMANAS_E_SOCIAIS', label: 'Ciências Humanas e Sociais' },
    { value: 'LETRAS', label: 'Letras' },
    { value: 'HISTORIA', label: 'História' },
    { value: 'GEOGRAFIA', label: 'Geografia' },
    { value: 'SOCIOLOGIA', label: 'Sociologia' },
    { value: 'JURIDICO', label: 'Jurídico' },
    { value: 'DIREITO_DIGITAL', label: 'Direito Digital' },
    { value: 'MEIO_AMBIENTE_E_SUSTENTABILIDADE', label: 'Meio Ambiente e Sustentabilidade' }
  ];

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
  
  onSearchDebounced() {
    clearTimeout(this.searchDebounceTimer);
    this.searchDebounceTimer = setTimeout(() => {
      this.onSearch();
    }, 500);
  }
  
  onSearch(): void {
    const specializations = this.specializationsInput
      ? this.specializationsInput.split(',').map(s => s.trim()).filter(Boolean)
      : [];

    if (this.selectedInterestAreas.length > 0 || specializations.length > 0) {
      const interestArea = this.selectedInterestAreas.length > 0 ? this.selectedInterestAreas[0] : undefined;

      this.profileService.searchMentors(interestArea, specializations).subscribe({
        next: (mentors) => {
          this.mentors = mentors;
          this.mentorSearchPerformed = true;
        },
        error: (err) => {
          console.error('Erro ao buscar mentores:', err);
          this.mentors = [];
          this.mentorSearchPerformed = true;
        }
      });
    } else {
      this.mentors = [];
      this.mentorSearchPerformed = false;
    }
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