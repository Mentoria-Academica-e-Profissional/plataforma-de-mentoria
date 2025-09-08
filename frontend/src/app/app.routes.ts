import { Routes } from '@angular/router';

import { LoginComponent } from './auth/login/login.component';
import { RegisterComponent } from './auth/register/register.component';
import { ProfileComponent } from './profile/profile.component';
import { ProfileService, ProfileData } from '../app/profile/profile.service';
import { UnauthorizedComponent } from './auth/unauthorized/unauthorized';
import { HomeComponent } from './home/home.component';
import { authGuard } from './auth/auth-guard';
import { SessionHistoryComponent } from './session-history/session-history.component';
import { MentorRegisterComponent } from './auth/register/mentor-register/mentor-register/mentor-register.component';
import { MentoredRegisterComponent } from './auth/register/mentored-register/mentored-register/mentored-register.component';
import { SessionComponent } from './sessions/session.component';
import { MaterialComponent } from './material/material.component';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },

  { path: 'login', component: LoginComponent, title: 'Login' },
  { path: 'register', component: RegisterComponent, title: 'Cadastro' },
  { path: 'register-mentor', component: MentorRegisterComponent, title: 'Finalizar Cadastro' },
  { path: 'register-mentored', component: MentoredRegisterComponent, title: 'Finalizar Cadastro' },
  { path: 'profile', component: ProfileComponent,  title: 'Perfil' },
  { path: 'home', component: HomeComponent, title: 'Plataforma de Mentoria', canActivate: [authGuard] },
  { path: 'sessions', component: SessionComponent , title: 'Sessões', canActivate: [authGuard]},
  { path: 'sessions/history', component: SessionHistoryComponent, title: 'Histórico de Sessões', canActivate: [authGuard] },
  { path: 'materials', component: MaterialComponent, title: 'Materiais de Apoio'},
  { path: 'unauthorized', component: UnauthorizedComponent, title: 'Não autorizado' },

  { path: '**', redirectTo: 'login' }
];
