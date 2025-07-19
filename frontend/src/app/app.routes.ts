// src/app/app.routes.ts
import { Routes } from '@angular/router';

import { LoginComponent } from './auth/login/login.component';
import { RegisterComponent } from './auth/register/register.component';
import { UnauthorizedComponent } from './auth/unauthorized/unauthorized';
import { HomeComponent } from './home/home.component';
import { authGuard } from './auth/auth-guard';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },

  { path: 'login', component: LoginComponent, title: 'Login' },
  { path: 'register', component: RegisterComponent, title: 'Cadastro' },

  { path: 'home', component: HomeComponent, title: 'Plataforma de Mentoria', canActivate: [authGuard] },
  { path: 'unauthorized', component: UnauthorizedComponent, title: 'Não autorizado' },

  { path: '**', redirectTo: 'login' }
];
