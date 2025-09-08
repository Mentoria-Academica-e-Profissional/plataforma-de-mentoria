import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Material, MaterialType } from '../entity/material';
import { MaterialService } from '../services/material';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-material-library',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './material-library.html',
  styleUrl: './material-library.css'
})
export class MaterialLibraryComponent implements OnInit {
  materials: Material[] = [];
  suggestedMaterials: Material[] = [];
  filteredMaterials: Material[] = [];
  selectedArea: string = '';
  selectedType: string = '';
  searchTerm: string = '';
  
  materialTypes = Object.values(MaterialType);
  interestAreas = [
    { value: 'TECNOLOGIA_DA_INFORMACAO', label: 'Tecnologia da Informação' },
    { value: 'DESENVOLVIMENTO_DE_SOFTWARE', label: 'Desenvolvimento de Software' },
    { value: 'CIENCIA_DE_DADOS_E_IA', label: 'Ciência de Dados e IA' },
    { value: 'CIBERSEGURANCA', label: 'Cibersegurança' },
    { value: 'UX_UI_DESIGN', label: 'UX/UI Design' },
    { value: 'ENGENHARIA_GERAL', label: 'Engenharia' },
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
    { value: 'ARTES_E_DESIGN', label: 'Artes e Design' },
    { value: 'CIENCIAS_HUMANAS_E_SOCIAIS', label: 'Ciências Humanas e Sociais' },
    { value: 'LETRAS', label: 'Letras' },
    { value: 'HISTORIA', label: 'História' },
    { value: 'GEOGRAFIA', label: 'Geografia' },
    { value: 'SOCIOLOGIA', label: 'Sociologia' },
    { value: 'JURIDICO', label: 'Jurídico' },
    { value: 'DIREITO_DIGITAL', label: 'Direito Digital' },
    { value: 'MEIO_AMBIENTE_E_SUSTENTABILIDADE', label: 'Meio Ambiente e Sustentabilidade' }
  ];

  materialService = inject(MaterialService);
  router = inject(Router);

  ngOnInit(): void {
    this.loadAllMaterials();
    this.loadSuggestedMaterials();
  }

  loadAllMaterials(): void {
    this.materialService.getAllMaterials().subscribe({
      next: (data) => {
        this.materials = data;
        this.filteredMaterials = data;
      },
      error: (err) => console.error('Erro ao carregar materiais:', err)
    });
  }

  loadSuggestedMaterials(): void {
    this.materialService.getSuggestedMaterials().subscribe({
      next: (data) => {
        this.suggestedMaterials = data;
      },
      error: (err) => console.error('Erro ao carregar materiais sugeridos:', err)
    });
  }

  applyFilters(): void {
    if (this.selectedArea) {
      this.materialService.filterByInterestAreas([this.selectedArea]).subscribe({
        next: (data) => {
          let filtered = [...data];
          if (this.selectedType) {
            filtered = filtered.filter(m => m.materialType === this.selectedType);
          }
          if (this.searchTerm.trim()) {
            const searchTermLower = this.searchTerm.toLowerCase();
            filtered = filtered.filter(m => 
              m.title.toLowerCase().includes(searchTermLower)
            );
          }
          this.filteredMaterials = filtered;
        },
        error: (err) => console.error('Erro ao filtrar por área:', err)
      });
    } else {
      let filtered = [...this.materials];
      if (this.selectedType) {
        filtered = filtered.filter(m => m.materialType === this.selectedType);
      }
      if (this.searchTerm.trim()) {
        const searchTermLower = this.searchTerm.toLowerCase();
        filtered = filtered.filter(m => 
          m.title.toLowerCase().includes(searchTermLower)
        );
      }
      this.filteredMaterials = filtered;
    }
  }

  getMaterialTypeName(type: MaterialType): string {
    switch(type) {
      case MaterialType.DOCUMENTO: return 'Documento';
      case MaterialType.VIDEO: return 'Vídeo';
      case MaterialType.LINK: return 'Link';
      default: return type as string;
    }
  }

  openMaterial(material: Material): void {
    if (material.materialType === MaterialType.LINK && material.url) {
      window.open(material.url, '_blank');
    } else if (material.filePath) {
      try {
        const baseUrl = environment.apiUrl.replace('/api', '');
        window.open(`${baseUrl}/upload/${material.filePath.split('/').pop()}`, '_blank');
      } catch (error) {
        console.error('Erro ao abrir arquivo:', error);
        alert('Não foi possível abrir o arquivo. Entre em contato com o administrador.');
      }
    } else {
      alert('Este material não possui um arquivo ou link associado.');
    }
  }
}
