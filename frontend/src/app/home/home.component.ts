import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { AuthService } from '../auth/auth.service';
import { Router } from '@angular/router';
import { Subject, debounceTime, distinctUntilChanged } from 'rxjs';
import { MentorService } from './mentor/mentor.component';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css',
})
export class HomeComponent implements OnInit, OnDestroy {
  title = 'Mentoria';

  private authService = inject(AuthService);
  private router = inject(Router);
  private mentorService = inject(MentorService);

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  isAuthenticated(): boolean {
    return this.authService.isAuthenticated();
  }

  hasMentorRole(): boolean {
    return this.authService.hasRole('MENTOR');
  }
  private searchSubject = new Subject<string>();
  
  ngOnInit() {
    this.searchSubject
      .pipe(debounceTime(322), distinctUntilChanged())
      .subscribe((searchTerm) => {
        this.searchMentoredByInterest(searchTerm);
      });
  }

  ngOnDestroy() {
    this.searchSubject.complete();
  }

  onSearchInput(value: string): void {
    this.searchMentorsByInterest(value);
  }

  performSearch(interest: string): void {
    if (!interest.trim()) {
      this.results = [];
      return;
    }
    this.mentorService.searchMentoredByInterest(interest).subscribe({
      next: (response) => {
        this.results = response;
        this.results.sort((a, b) => a.fullName.localeCompare(b.fullName));
        console.log('Search results from API:', this.results);
      },
      error: (error) => {
        console.error('Error searching mentored:', error);
        this.results = this.mentoredMockList.filter((mentored) =>
          mentored.interests.some((i: string) =>
            i.toLowerCase().includes(interest.toLowerCase())
          )
        );
        this.results.sort((a, b) => a.name.localeCompare(b.name));
      },
      // this.results = this.mentoredMockList.filter((mentored) =>
      //   mentored.interests.some((i: string) =>
      //     i.toLowerCase().includes(interest.toLowerCase())
      //   )
      // );
      // this.results.sort((a, b) => a.name.localeCompare(b.name));
      // console.log('Search results:', this.results);
    });
  }


  results: any[] = [];


  searchMentoredByInterest(interest: string): void {
    this.performSearch(interest);
  }

  
  mentorResults: any[] = [];

searchMentorsByInterest(interest: string): void {
  if (!interest.trim()) {
    this.mentorResults = [];
    return;
  }
  this.mentorResults = this.mentorsMockList.filter((mentor) =>
    mentor.interests.some((i: string) =>
      i.toLowerCase().includes(interest.toLowerCase())
    ) ||
    mentor.specialization.toLowerCase().includes(interest.toLowerCase())
  );
  this.mentorResults.sort((a, b) => a.name.localeCompare(b.name));
}
  mentoredMockList: any[] = [
    {
      id: 1,
      name: 'João Silva',
      interests: ['Tecnologia', 'Carreira', 'Programação'],
      course: 'Engenharia de Software',
    },
    {
      id: 2,
      name: 'Maria Oliveira',
      interests: ['Empreendedorismo', 'Inovação', 'Tecnologia'],
      course: 'Administração de Empresas',
    },
    {
      id: 3,
      name: 'Carlos Pereira',
      interests: ['Desenvolvimento Pessoal', 'Liderança', 'Carreira'],
      course: 'Gestão de Recursos Humanos',
    },
    {
      id: 4,
      name: 'Ana Costa',
      interests: ['Programação', 'Inteligência Artificial', 'Tecnologia'],
      course: 'Ciência da Computação',
    },
    {
      id: 5,
      name: 'Pedro Santos',
      interests: ['Empreendedorismo', 'Marketing Digital', 'Inovação'],
      course: 'Marketing Digital',
    },
    {
      id: 6,
      name: 'Juliana Lima',
      interests: ['Carreira', 'Desenvolvimento Pessoal', 'Comunicação'],
      course: 'Psicologia Organizacional',
    },
    {
      id: 7,
      name: 'Rafael Mendes',
      interests: ['Tecnologia', 'Programação', 'DevOps'],
      course: 'Engenharia de Computação',
    },
    {
      id: 8,
      name: 'Fernanda Rocha',
      interests: ['Liderança', 'Gestão de Projetos', 'Desenvolvimento Pessoal'],
      course: 'Gestão de Projetos',
    },
    {
      id: 9,
      name: 'Lucas Barbosa',
      interests: ['Inovação', 'Tecnologia', 'Startups'],
      course: 'Engenharia de Produção',
    },
    {
      id: 10,
      name: 'Camila Ferreira',
      interests: ['Marketing Digital', 'Empreendedorismo', 'E-commerce'],
      course: 'Publicidade e Propaganda',
    },
    {
      id: 11,
      name: 'Bruno Alves',
      interests: ['Programação', 'Carreira', 'Tecnologia'],
      course: 'Análise e Desenvolvimento de Sistemas',
    },
    {
      id: 12,
      name: 'Larissa Gomes',
      interests: ['Comunicação', 'Liderança', 'Gestão de Pessoas'],
      course: 'Comunicação Social',
    },
    {
      id: 13,
      name: 'Thiago Nascimento',
      interests: ['Inteligência Artificial', 'Programação', 'Pesquisa'],
      course: 'Mestrado em Inteligência Artificial',
    },
    {
      id: 14,
      name: 'Isabela Martins',
      interests: ['Startups', 'Empreendedorismo', 'Investimentos'],
      course: 'Economia',
    },
    {
      id: 15,
      name: 'Gabriel Rodrigues',
      interests: ['DevOps', 'Tecnologia', 'Infraestrutura'],
      course: 'Sistemas de Informação',
    },
  ];

  mentorsMockList: any[] = [
  {
    id: 1,
    name: 'Alice Mentor',
    interests: ['Programação', 'Angular', 'Carreira'],
    specialization: 'Desenvolvimento Web',
  },
  {
    id: 2,
    name: 'Bob Mentor',
    interests: ['Inovação', 'Startups'],
    specialization: 'Empreendedorismo',
  },
  {
    id: 3,
    name: 'Charlie Mentor',
    interests: ['Tecnologia', 'Inovação'],
    specialization: 'Desenvolvimento de Software',
  },
  {
    id: 4,
    name: 'Diana Mentor',
    interests: ['Carreira', 'Mentoria'],
    specialization: 'Coaching',
  },
  {
    id: 5,
    name: 'Eva Mentor',
    interests: ['Marketing', 'Vendas'],
    specialization: 'Estratégia de Negócios',
  },
];

}
