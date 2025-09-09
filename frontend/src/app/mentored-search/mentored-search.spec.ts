import { Component } from '@angular/core';

@Component({
  selector: 'app-mentored-search',
  templateUrl: './mentored-search.component.html',
  styleUrls: ['./mentored-search.component.css']
})
export class MentoredSearchComponent {

  selectedInterestAreas: string[] = [];

  clearInterestAreas(): void {
    this.selectedInterestAreas = [];
    this.onSearch();
  }

  getInitials(name: string): string {
    if (!name) return '';
    const names = name.trim().split(/\s+/);
    const firstInitial = names[0] && names[0].length ? names[0][0] : '';
    const lastInitial =
      names.length > 1 && names[names.length - 1].length
        ? names[names.length - 1][0]
        : '';
    return (firstInitial + lastInitial).toUpperCase();
  }

  private courseMap: { [key: string]: string } = {
    ADMINISTRACAO: 'Administração',
    DIREITO: 'Direito',
    MEDICINA: 'Medicina',
    ENGENHARIA_CIVIL: 'Engenharia Civil',
    CIENCIA_DA_COMPUTACAO: 'Ciência da Computação',
    PSICOLOGIA: 'Psicologia',
    ENFERMAGEM: 'Enfermagem',
    ARQUITETURA_E_URBANISMO: 'Arquitetura e Urbanismo',
    CONTABILIDADE: 'Ciências Contábeis',
    ODONTOLOGIA: 'Odontologia',
    PEDAGOGIA: 'Pedagogia',
    FISIOTERAPIA: 'Fisioterapia',
    NUTRICIONISMO: 'Nutrição',
    EDUCACAO_FISICA: 'Educação Física',
    VETERINARIA: 'Medicina Veterinária',
    ZOOTECNIA: 'Zootecnia',
    LETRAS: 'Letras'
  };

  getCourseName(courseKey: string): string {
    return this.courseMap[courseKey] || courseKey;
  }

  private onSearch(): void {
    console.log('Busca atualizada com áreas:', this.selectedInterestAreas);
  }
}
