import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { ProfileService, ProfileData } from './profile.service';
import { EditMentoredModalComponent } from '../auth/edit-mentored-profile-modal/edit-mentored-modal.component';
import { EditMentorProfileModalComponent } from '../auth/edit-mentor-profile-modal/edit-mentor-profile-modal.component';
import { DeleteMentorModalComponent } from '../auth/delete-mentor-modal/delete-mentor-modal.component';
import { DeleteMentoredModalComponent } from '../auth/delete-mentored-modal/delete-mentored-modal.component';
import { AuthService } from '../auth/auth.service';

@Component({
  selector: 'app-perfil',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    EditMentorProfileModalComponent,
    EditMentoredModalComponent,
    DeleteMentorModalComponent,
    DeleteMentoredModalComponent
  ],
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {
  private profileService = inject(ProfileService);
  private authService = inject(AuthService);
  private router = inject(Router);

  profileData: ProfileData | null = null;
  loading = true;
  error: string | null = null;
  showEditModal = false;
  showDeleteModal = false;

  ngOnInit(): void {
    this.loadProfile();
  }

  goBack(): void {
    this.router.navigate(['/home']);
  }

  loadProfile(): void {
    this.loading = true;
    this.error = null;

    this.profileService.getCompleteProfile().subscribe({
      next: (data: ProfileData) => {
        this.profileData = data;
        this.loading = false;
        console.log('Profile loaded:', data);
      },
      error: (err: any) => {
        this.error = 'Erro ao carregar perfil';
        this.loading = false;
        console.error('Error loading profile:', err);
      }
    });
  }

  editProfile(): void {
    this.showEditModal = true;
  }

  closeEditModal(): void {
    this.showEditModal = false;
  }

  saveMentorProfile(updatedData: any): void {
    if (!this.profileData || this.profileData.type !== 'MENTOR') return;

    const mentorId = this.profileData.data.id;
    this.profileService.updateMentorProfile(mentorId, updatedData).subscribe({
      next: (response: any) => {
        console.log('Perfil atualizado com sucesso:', response);
        this.loadProfile();
        this.closeEditModal();
      },
      error: (err: any) => {
        console.error('Erro ao atualizar perfil:', err);
        this.error = 'Erro ao atualizar perfil';
      }
    });
  }

  saveMentoredProfile(updatedData: any): void {
    if (!this.profileData) return;

    this.profileService.updateMentoredProfile(updatedData).subscribe({
      next: (response: any) => {
        console.log('Profile updated successfully:', response);
        this.loadProfile();
        this.closeEditModal();
      },
      error: (err: any) => {
        console.error('Erro ao atualizar perfil:', err);
        this.error = 'Erro ao atualizar perfil';
      }
    });
  }

  openDeleteModal(): void {
    if (this.profileData?.type === 'MENTOR' || this.profileData?.type === 'MENTORADO') {
      this.showDeleteModal = true;
    } else {
      console.error('Tipo de perfil não suportado para exclusão.');
    }
  }

  async confirmDelete(): Promise<void> {
    if (!this.profileData) return;

    const id = this.profileData.data.id;
    try {
      if (this.profileData.type === 'MENTOR') {
        await this.authService.deleteMentor(id);
      } else if (this.profileData.type === 'MENTORADO') {
        await this.authService.deleteMentored(id);
      }

      this.showDeleteModal = false;
      this.authService.logout();
      window.location.href = '/login'; 
    } catch (error) {
      console.error('Erro ao deletar conta:', error);
      this.error = 'Erro ao deletar conta';
    }
  }

  cancelDelete(): void {
    this.showDeleteModal = false;
  }

  getSpecializations(): string[] {
    if (!this.profileData?.data?.specializations) return [];
    if (Array.isArray(this.profileData.data.specializations)) {
      return this.profileData.data.specializations;
    }
    return this.profileData.data.specializations.split(',').map((s: string) => s.trim());
  }

  getInitials(): string {
    const fullName = this.profileData?.data?.fullName || '';
    const names = fullName.trim().split(' ');

    if (names.length === 0) return '';

    const firstInitial = names[0]?.[0] || '';
    const lastInitial = names.length > 1 ? names[names.length - 1]?.[0] : '';

    return (firstInitial + lastInitial).toUpperCase();
  }


getInterestAreas(): string[] {
  if (!this.profileData?.data?.interestArea) return [];
  
  if (Array.isArray(this.profileData.data.interestArea)) {
    return this.profileData.data.interestArea.map((area: string) => this.formatInterestAreaName(area));
  }
  
  if (typeof this.profileData.data.interestArea === 'string') {
    return this.profileData.data.interestArea
      .split(',')
      .map((area: string) => this.formatInterestAreaName(area.trim()))
      .filter((area: string) => area.length > 0);
  }
  
  return [];
}

private formatInterestAreaName(area: string): string {
  const areaNames: { [key: string]: string } = {
    'TECNOLOGIA_DA_INFORMACAO': 'Tecnologia da Informação',
    'DESENVOLVIMENTO_DE_SOFTWARE': 'Desenvolvimento de Software',
    'CIENCIA_DE_DADOS_E_IA': 'Ciência de Dados e Inteligência Artificial',
    'CIBERSEGURANCA': 'Cibersegurança',
    'UX_UI_DESIGN': 'UX/UI Design',
    'ENGENHARIA_GERAL': 'Engenharia',
    'ENGENHARIA_CIVIL': 'Engenharia Civil',
    'ENGENHARIA_DE_PRODUCAO': 'Engenharia de Produção',
    'MATEMATICA_E_ESTATISTICA': 'Matemática e Estatística',
    'FISICA': 'Física',
    'ADMINISTRACAO_E_GESTAO': 'Administração e Gestão',
    'EMPREENDEDORISMO_E_INOVACAO': 'Empreendedorismo e Inovação',
    'FINANCAS_E_CONTABILIDADE': 'Finanças e Contabilidade',
    'RECURSOS_HUMANOS': 'Recursos Humanos',
    'LOGISTICA_E_CADEIA_DE_SUPRIMENTOS': 'Logística e Cadeia de Suprimentos',
    'MARKETING_E_COMUNICACAO': 'Marketing e Comunicação',
    'MARKETING_DIGITAL': 'Marketing Digital',
    'JORNALISMO': 'Jornalismo',
    'PUBLICIDADE_E_PROPAGANDA': 'Publicidade e Propaganda',
    'COMUNICACAO_INSTITUCIONAL': 'Comunicação Institucional',
    'CIENCIAS_BIOLOGICAS_E_SAUDE': 'Ciências Biológicas e Saúde',
    'MEDICINA': 'Medicina',
    'PSICOLOGIA': 'Psicologia',
    'NUTRICAO': 'Nutrição',
    'BIOTECNOLOGIA': 'Biotecnologia',
    'EDUCACAO': 'Educação',
    'CIENCIAS_HUMANAS_E_SOCIAIS': 'Ciências Humanas e Sociais',
    'LETRAS': 'Letras',
    'HISTORIA': 'História',
    'GEOGRAFIA': 'Geografia',
    'SOCIOLOGIA': 'Sociologia',
    'JURIDICO': 'Jurídico',
    'DIREITO_DIGITAL': 'Direito Digital',
    'MEIO_AMBIENTE_E_SUSTENTABILIDADE': 'Meio Ambiente e Sustentabilidade'
  };
  
  return areaNames[area] || area;
}

  private affiliationTypeMap: { [key: string]: string } = {
    'DOCENTE': 'Docente',
    'TECNICO_ADMINISTRATIVO': 'Técnico Administrativo',
    'ALUNO_POS_GRADUACAO': 'Aluno de Pós-Graduação',
    'PESQUISADOR': 'Pesquisador',
    'GESTOR': 'Gestor',
    'TERCEIRIZADO': 'Terceirizado'
  };

  private courseMap: { [key: string]: string } = {
    'ADMINISTRACAO': 'Administração',
    'DIREITO': 'Direito',
    'MEDICINA': 'Medicina',
    'ENGENHARIA_CIVIL': 'Engenharia Civil',
    'CIENCIA_DA_COMPUTACAO': 'Ciência da Computação',
    'PSICOLOGIA': 'Psicologia',
    'ENFERMAGEM': 'Enfermagem',
    'ARQUITETURA_E_URBANISMO': 'Arquitetura e Urbanismo',
    'CONTABILIDADE': 'Ciências Contábeis',
    'ODONTOLOGIA': 'Odontologia',
    'PEDAGOGIA': 'Pedagogia',
    'FISIOTERAPIA': 'Fisioterapia',
    'NUTRICIONISMO': 'Nutrição',
    'EDUCACAO_FISICA': 'Educação Física',
    'VETERINARIA': 'Medicina Veterinária',
    'ZOOTECNIA': 'Zootecnia',
    'LETRAS': 'Letras'
  };

  getAffiliationTypeName(affiliationType: string): string {
    return this.affiliationTypeMap[affiliationType] || affiliationType || 'Não informado';
  }


  goToFeedback(): void {
    this.router.navigate(['/profile/feedback']);
  }
  
  getCourseName(course: string): string {
    return this.courseMap[course] || course || 'Não informado';
  }
}
