import { Component, OnInit } from '@angular/core';
import { CommonModule, Location } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators, FormArray } from '@angular/forms';
import { MaterialService } from './material.service';
import { Material, InterestArea, InterestAreaLabels, MaterialType } from './material-module';

@Component({
  selector: 'app-material',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule
  ],
  templateUrl: './material.component.html',
  styleUrls: ['./material.component.css']
})
export class MaterialComponent implements OnInit {
  // --- Propriedades de Estado ---
  materials: Material[] = [];
  filteredMaterials: Material[] = [];
  isLoading = false;
  
  // --- Formulários ---
  materialForm: FormGroup;
  filterForm: FormGroup;

  // --- Controle da UI ---
  showFilterForm = false;
  selectedFile: File | null = null;
  isModalOpen = false;
  modalMode: 'add' | 'edit' = 'add';
  openMenuId: number | null = null;
  isTypeDropdownOpen = false; // Adicionado para controlar o dropdown de tipo
  
  // Controle das seções expansíveis
  expandedSections: { [key: string]: boolean } = {};

  // --- Dados Estáticos ---
  interestAreas = Object.values(InterestArea);
  interestAreaLabels = InterestAreaLabels;
  materialTypes = Object.values(MaterialType);

  // Estrutura para agrupar as áreas de interesse
  groupedInterestAreas: { name: string; key: string; items: { value: InterestArea; label: string; }[] }[] = [];

  constructor(
    private materialService: MaterialService,
    private fb: FormBuilder,
    private location: Location
  ) {
    this.materialForm = this.fb.group({
      id: [null],
      title: ['', [Validators.required, Validators.maxLength(180)]],
      materialType: [null, [Validators.required]],
      url: [''],
      interestArea: this.fb.array([], [Validators.required])
    });

    this.filterForm = this.fb.group({
      interestArea: this.fb.array([])
    });

    this.initializeGroupedAreas();
  }

  ngOnInit(): void {
    this.loadAllMaterials();
  }

  // --- Getters para Acesso Fácil ---
  get materialInterestArray(): FormArray {
    return this.materialForm.get('interestArea') as FormArray;
  }

  get filterInterestArray(): FormArray {
    return this.filterForm.get('interestArea') as FormArray;
  }

  // --- Carregamento de Dados ---
  loadAllMaterials(): void {
    this.isLoading = true;
    this.materialService.getAllMaterials().subscribe({
      next: (materials) => {
        this.materials = materials.sort((a, b) => new Date(b.createdAt!).getTime() - new Date(a.createdAt!).getTime());
        this.filteredMaterials = this.materials;
        this.isLoading = false;
      },
      error: () => this.isLoading = false
    });
  }

  // --- Lógica do Modal ---
  openModal(mode: 'add' | 'edit', material: Material | null = null): void {
    this.modalMode = mode;
    this.openMenuId = null;
    
    if (mode === 'edit' && material) {
      this.materialForm.patchValue(material);
      this.materialInterestArray.clear();
      material.interestArea.forEach(area => this.materialInterestArray.push(this.fb.control(area)));
    } else {
      this.materialForm.reset({ materialType: null });
      this.materialInterestArray.clear();
    }
    this.isModalOpen = true;
  }

  closeModal(): void {
    this.isModalOpen = false;
    this.selectedFile = null;
    this.isTypeDropdownOpen = false;
    this.expandedSections = {};
  }

  saveMaterial(): void {
    if (!this.materialForm.valid) return;
    this.isLoading = true;

    const formValue = this.materialForm.value;
    const materialData: Material = {
      id: formValue.id,
      title: formValue.title,
      materialType: formValue.materialType,
      url: formValue.url || undefined,
      interestArea: this.materialInterestArray.value
    };

    const operation = this.modalMode === 'add'
      ? this.materialService.createMaterial(materialData, this.selectedFile || undefined)
      : this.materialService.updateMaterial(formValue.id, materialData, this.selectedFile || undefined);

    operation.subscribe({
      next: () => {
        this.loadAllMaterials();
        this.closeModal();
      },
      error: (err) => {
        console.error("Erro ao salvar material", err);
        this.isLoading = false;
      }
    });
  }
  
  deleteMaterial(id: number): void {
    this.openMenuId = null;
    setTimeout(() => {
      if (confirm('Tem certeza que deseja excluir este material?')) {
        this.materialService.deleteMaterial(id).subscribe({
          next: () => this.loadAllMaterials(),
          error: (err) => console.error('Erro ao deletar material', err)
        });
      }
    }, 10);
  }

  // --- Lógica do Menu de Ações e Filtros ---
  toggleActionMenu(event: MouseEvent, materialId: number): void {
    event.stopPropagation();
    this.openMenuId = this.openMenuId === materialId ? null : materialId;
  }
  
  toggleAreaSelection(area: InterestArea): void {
    const array = this.filterInterestArray;
    const index = array.controls.findIndex(x => x.value === area);
    if (index === -1) array.push(this.fb.control(area));
    else array.removeAt(index);
    this.filterMaterials();
  }

  isAreaSelected(area: InterestArea): boolean {
    return this.filterInterestArray.value.includes(area);
  }

  filterMaterials(): void {
    const selectedAreas = this.filterInterestArray.value;
    if (selectedAreas.length === 0) {
      this.filteredMaterials = this.materials;
      return;
    }
    this.filteredMaterials = this.materials.filter(m => 
      selectedAreas.some((area: InterestArea) => m.interestArea.includes(area))
    );
  }

  clearFilters(): void {
    this.filterInterestArray.clear();
    this.filteredMaterials = this.materials;
  }

  toggleFilterForm(): void {
    this.showFilterForm = !this.showFilterForm;
  }

  // --- Handlers de Formulário ---
  selectType(type: MaterialType): void {
    this.materialForm.get('materialType')?.setValue(type);
    this.isTypeDropdownOpen = false;
    this.onMaterialTypeChange();
  }

  onMaterialInterestChange(event: any): void {
    const array = this.materialInterestArray;
    const value = event.target.value;
    if (event.target.checked) {
      array.push(this.fb.control(value));
    } else {
      const index = array.controls.findIndex(x => x.value === value);
      if (index !== -1) array.removeAt(index);
    }
  }

  onFileSelected(event: any): void {
    const file = event.target.files?.[0];
    if (file) this.selectedFile = file;
  }
  
  onMaterialTypeChange(): void {
    const materialType = this.materialForm.get('materialType')?.value;
    const urlControl = this.materialForm.get('url');
    if (materialType === MaterialType.LINK) {
      urlControl?.setValidators([Validators.required]);
    } else {
      urlControl?.clearValidators();
    }
    urlControl?.updateValueAndValidity();
  }
  
  toggleSection(section: string): void {
    this.expandedSections[section] = !this.expandedSections[section];
  }

  // --- Métodos Utilitários ---
  private initializeGroupedAreas(): void {
    this.groupedInterestAreas = [
      { name: 'Tecnologia', key: 'tecnologia', items: [
        { value: InterestArea.TECNOLOGIA_DA_INFORMACAO, label: this.interestAreaLabels[InterestArea.TECNOLOGIA_DA_INFORMACAO] },
        { value: InterestArea.DESENVOLVIMENTO_DE_SOFTWARE, label: this.interestAreaLabels[InterestArea.DESENVOLVIMENTO_DE_SOFTWARE] },
        { value: InterestArea.CIENCIA_DE_DADOS_E_IA, label: this.interestAreaLabels[InterestArea.CIENCIA_DE_DADOS_E_IA] },
        { value: InterestArea.CIBERSEGURANCA, label: this.interestAreaLabels[InterestArea.CIBERSEGURANCA] },
        { value: InterestArea.UX_UI_DESIGN, label: this.interestAreaLabels[InterestArea.UX_UI_DESIGN] }
      ]},
      { name: 'Engenharia e Ciências Exatas', key: 'engenharia', items: [
        { value: InterestArea.ENGENHARIA_GERAL, label: this.interestAreaLabels[InterestArea.ENGENHARIA_GERAL] },
        { value: InterestArea.ENGENHARIA_CIVIL, label: this.interestAreaLabels[InterestArea.ENGENHARIA_CIVIL] },
        { value: InterestArea.ENGENHARIA_DE_PRODUCAO, label: this.interestAreaLabels[InterestArea.ENGENHARIA_DE_PRODUCAO] },
        { value: InterestArea.MATEMATICA_E_ESTATISTICA, label: this.interestAreaLabels[InterestArea.MATEMATICA_E_ESTATISTICA] },
        { value: InterestArea.FISICA, label: this.interestAreaLabels[InterestArea.FISICA] }
      ]},
      { name: 'Gestão e Negócios', key: 'gestao', items: [
        { value: InterestArea.ADMINISTRACAO_E_GESTAO, label: this.interestAreaLabels[InterestArea.ADMINISTRACAO_E_GESTAO] },
        { value: InterestArea.EMPREENDEDORISMO_E_INOVACAO, label: this.interestAreaLabels[InterestArea.EMPREENDEDORISMO_E_INOVACAO] },
        { value: InterestArea.FINANCAS_E_CONTABILIDADE, label: this.interestAreaLabels[InterestArea.FINANCAS_E_CONTABILIDADE] },
        { value: InterestArea.RECURSOS_HUMANOS, label: this.interestAreaLabels[InterestArea.RECURSOS_HUMANOS] },
        { value: InterestArea.LOGISTICA_E_CADEIA_DE_SUPRIMENTOS, label: this.interestAreaLabels[InterestArea.LOGISTICA_E_CADEIA_DE_SUPRIMENTOS] }
      ]},
      { name: 'Comunicação e Marketing', key: 'comunicacao', items: [
        { value: InterestArea.MARKETING_E_COMUNICACAO, label: this.interestAreaLabels[InterestArea.MARKETING_E_COMUNICACAO] },
        { value: InterestArea.MARKETING_DIGITAL, label: this.interestAreaLabels[InterestArea.MARKETING_DIGITAL] },
        { value: InterestArea.JORNALISMO, label: this.interestAreaLabels[InterestArea.JORNALISMO] },
        { value: InterestArea.PUBLICIDADE_E_PROPAGANDA, label: this.interestAreaLabels[InterestArea.PUBLICIDADE_E_PROPAGANDA] },
        { value: InterestArea.COMUNICACAO_INSTITUCIONAL, label: this.interestAreaLabels[InterestArea.COMUNICACAO_INSTITUCIONAL] }
      ]},
      { name: 'Saúde e Ciências Biológicas', key: 'saude', items: [
        { value: InterestArea.CIENCIAS_BIOLOGICAS_E_SAUDE, label: this.interestAreaLabels[InterestArea.CIENCIAS_BIOLOGICAS_E_SAUDE] },
        { value: InterestArea.MEDICINA, label: this.interestAreaLabels[InterestArea.MEDICINA] },
        { value: InterestArea.PSICOLOGIA, label: this.interestAreaLabels[InterestArea.PSICOLOGIA] },
        { value: InterestArea.NUTRICAO, label: this.interestAreaLabels[InterestArea.NUTRICAO] },
        { value: InterestArea.BIOTECNOLOGIA, label: this.interestAreaLabels[InterestArea.BIOTECNOLOGIA] }
      ]},
      { name: 'Educação e Ciências Humanas', key: 'educacao', items: [
        { value: InterestArea.EDUCACAO, label: this.interestAreaLabels[InterestArea.EDUCACAO] },
        { value: InterestArea.CIENCIAS_HUMANAS_E_SOCIAIS, label: this.interestAreaLabels[InterestArea.CIENCIAS_HUMANAS_E_SOCIAIS] },
        { value: InterestArea.LETRAS, label: this.interestAreaLabels[InterestArea.LETRAS] },
        { value: InterestArea.HISTORIA, label: this.interestAreaLabels[InterestArea.HISTORIA] },
        { value: InterestArea.GEOGRAFIA, label: this.interestAreaLabels[InterestArea.GEOGRAFIA] },
        { value: InterestArea.SOCIOLOGIA, label: this.interestAreaLabels[InterestArea.SOCIOLOGIA] }
      ]},
      { name: 'Jurídico e Sustentabilidade', key: 'juridico', items: [
        { value: InterestArea.JURIDICO, label: this.interestAreaLabels[InterestArea.JURIDICO] },
        { value: InterestArea.DIREITO_DIGITAL, label: this.interestAreaLabels[InterestArea.DIREITO_DIGITAL] },
        { value: InterestArea.MEIO_AMBIENTE_E_SUSTENTABILIDADE, label: this.interestAreaLabels[InterestArea.MEIO_AMBIENTE_E_SUSTENTABILIDADE] }
      ]},
    ];
  }

  goBack(): void { this.location.back(); }
  
  downloadFile(material: Material): void {
    if (material.filePath) {
      const url = this.materialService.getDownloadUrl(material.filePath);
      window.open(url, '_blank');
    }
  }

  formatDate(date: Date | string | undefined): string {
    if (!date) return '';
    return new Date(date).toLocaleDateString('pt-BR');
  }

  getMaterialTypeLabel(type: MaterialType | null): string {
    if (!type) return 'Selecione uma opção';
    switch (type) {
      case MaterialType.DOCUMENTO: return 'Documento';
      case MaterialType.VIDEO: return 'Vídeo';
      case MaterialType.LINK: return 'Link';
      default: return type as string;
    }
  }

  openUrl(url: string): void {
    window.open(url, '_blank');
  }
}