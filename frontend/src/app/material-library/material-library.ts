import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Material, MaterialType } from '../entity/material';
import { MaterialService } from '../services/material';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment';
import { catchError, retry } from 'rxjs/operators';
import { of } from 'rxjs';
import { AuthService } from '../auth/auth.service';

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
  isLoading: boolean = false;
  loadingInterestAreas: boolean = false;
  loadingError: boolean = false;
  errorMessage: string = '';
  materialTypes = Object.values(MaterialType);
  interestAreas: {value: string, label: string}[] = [];
  
  
  fallbackInterestAreas = [
    { value: 'TECNOLOGIA_DA_INFORMACAO', label: 'Tecnologia da Informação' },
    { value: 'DESENVOLVIMENTO_DE_SOFTWARE', label: 'Desenvolvimento de Software' },
    { value: 'CIENCIA_DE_DADOS_E_IA', label: 'Ciência de Dados e IA' },
    { value: 'CIBERSEGURANCA', label: 'Cibersegurança' },
    { value: 'UX_UI_DESIGN', label: 'UX/UI Design' },
    { value: 'ENGENHARIA_GERAL', label: 'Engenharia' },
    { value: 'ENGENHARIA_CIVIL', label: 'Engenharia Civil' },
    { value: 'ENGENHARIA_DE_PRODUCAO', label: 'Engenharia de Produção' },
    
  ];

  materialService = inject(MaterialService);
  router = inject(Router);
  authService = inject(AuthService);

  ngOnInit(): void {
    console.log('MaterialLibraryComponent inicializado');
    console.log('URL da API configurada:', environment.apiUrl);
    this.checkBackendAvailability();
    this.loadAllMaterials();
    this.loadSuggestedMaterials();
    this.loadInterestAreas();
  }
  
  loadInterestAreas(): void {
    console.log('Carregando áreas de interesse da API...');
    this.loadingInterestAreas = true;
    
    this.materialService.getInterestAreas()
      .pipe(
        retry(1),
        catchError(err => {
          console.error('Erro ao carregar áreas de interesse da API:', err);
          console.log('Usando lista de áreas de interesse de backup');
          // Em caso de erro, usa a lista de backup
          return of(this.fallbackInterestAreas);
        })
      )
      .subscribe({
        next: (data) => {
          if (data && data.length > 0) {
            this.interestAreas = data;
            console.log(`${this.interestAreas.length} áreas de interesse carregadas com sucesso`);
          } else {
            console.warn('API retornou uma lista vazia de áreas de interesse, usando lista de backup');
            this.interestAreas = this.fallbackInterestAreas;
          }
          this.loadingInterestAreas = false;
        },
        error: () => {
          this.loadingInterestAreas = false;
        },
        complete: () => {
          this.loadingInterestAreas = false;
        }
      });
  }
  
  checkBackendAvailability(): void {
    fetch(environment.apiUrl + '/api/materiais', { method: 'GET' })
      .then(response => {
        if (response.ok) {
          console.log('Backend está acessível');
        } else {
          console.warn('Backend retornou status:', response.status);
          if (response.status === 401 || response.status === 403) {
            console.log('Backend está acessível, mas requer autenticação');
          } else {
            this.errorMessage = `Servidor respondeu com status ${response.status}. Verifique se o backend está configurado corretamente.`;
            this.loadingError = true;
          }
        }
      })
      .catch(error => {
        console.error('Erro ao verificar disponibilidade do backend:', error);
        this.errorMessage = 'Não foi possível conectar ao servidor. Verifique se o backend está rodando.';
        this.loadingError = true;
      });
  }

  loadAllMaterials(): void {
    this.isLoading = true;
    this.loadingError = false;
    this.errorMessage = '';
    this.materialService.getAllMaterials()
      .pipe(
        retry(2),
        catchError(err => {
          console.error('Erro ao carregar materiais:', err);
          this.loadingError = true;
          this.errorMessage = 'Não foi possível carregar a lista de materiais. Tente novamente mais tarde.';
          this.materials = [];
          this.filteredMaterials = [];
          return of([] as Material[]);
        })
      )
      .subscribe({
        next: (data) => {
          this.materials = data;
          this.filteredMaterials = data;
          this.isLoading = false;
        },
        complete: () => {
          this.isLoading = false;
        }
      });
  }

  loadSuggestedMaterials(): void {
    if (!this.authService.isAuthenticated()) {
      console.log('Usuário não está autenticado. Não é possível carregar sugestões personalizadas.');
      this.suggestedMaterials = [];
      return;
    }
    this.materialService.getSuggestedMaterials()
      .pipe(
        retry(1),
        catchError(err => {
          if (err.status === 500) {
            console.log('Erro 500 ao carregar sugestões: isso pode ser normal se não houver sugestões disponíveis');
          } else {
            console.error('Erro ao carregar materiais sugeridos:', err);
          }
          this.suggestedMaterials = [];
          return of([] as Material[]);
        })
      )
      .subscribe({
        next: (data) => {
          this.suggestedMaterials = data;
        }
      });
  }

  applyFilters(): void {
    this.isLoading = true;
    if (this.selectedArea) {
      this.materialService.filterByInterestAreas([this.selectedArea])
        .pipe(
          retry(1),
          catchError(err => {
            console.error('Erro ao filtrar por área:', err);
            this.errorMessage = 'Erro ao aplicar filtros. Tente novamente.';
            this.loadingError = true;
            return of([] as Material[]);
          })
        )
        .subscribe({
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
            this.isLoading = false;
          },
          complete: () => {
            this.isLoading = false;
          }
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
      this.isLoading = false;
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
        const baseUrl = environment.apiUrl;
        let fileUrl = '';
        if (material.filePath.startsWith('http')) {
          fileUrl = material.filePath;
        } else {
          const apiBase = baseUrl.includes('/api') ? baseUrl.replace('/api', '') : baseUrl;
          fileUrl = `${apiBase}/upload/${material.filePath.split('/').pop()}`;
        }
        console.log('Abrindo arquivo:', fileUrl);
        window.open(fileUrl, '_blank');
      } catch (error) {
        console.error('Erro ao abrir arquivo:', error);
        alert('Não foi possível abrir o arquivo. Entre em contato com o administrador.');
      }
    } else {
      alert('Este material não possui um arquivo ou link associado.');
    }
  }

  clearFilters(): void {
    this.selectedArea = '';
    this.selectedType = '';
    this.searchTerm = '';
    this.loadingError = false;
    this.errorMessage = '';
    this.loadAllMaterials();
  }
  
  retryLoading(): void {
    this.loadAllMaterials();
  }
}
