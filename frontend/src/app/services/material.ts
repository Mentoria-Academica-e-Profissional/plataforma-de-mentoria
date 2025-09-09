import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError, of } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { Material } from '../entity/material';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class MaterialService {
  private apiUrl = `${environment.apiUrl}/api/materiais`;

  constructor(private http: HttpClient) { }
  
  private handleError(error: HttpErrorResponse) {
    console.error('API Error:', error);
    
    if (error.status === 0) {
      console.error('Erro de conexão. Verifique sua conexão com a Internet ou se o servidor está rodando.');
    } else if (error.status === 404) {
      console.error('Endpoint não encontrado:', error.url);
    } else {
      console.error(`API retornou código ${error.status}, corpo:`, error.error);
    }
    
    return throwError(() => new Error(`Erro na API: ${error.message}`));
  }

  getAllMaterials(): Observable<Material[]> {
    console.log('Buscando todos os materiais na URL:', this.apiUrl);
    return this.http.get<Material[]>(this.apiUrl).pipe(
      tap(data => console.log('Materiais carregados:', data.length)),
      catchError(this.handleError)
    );
  }

  getMaterialById(id: number): Observable<Material> {
    console.log(`Buscando material com ID ${id}`);
    return this.http.get<Material>(`${this.apiUrl}/${id}`).pipe(
      tap(data => console.log('Material carregado:', data)),
      catchError(this.handleError)
    );
  }

  getSuggestedMaterials(): Observable<Material[]> {
    console.log('Buscando materiais sugeridos');
    return this.http.get<Material[]>(`${this.apiUrl}/sugestoes`).pipe(
      tap(data => console.log('Materiais sugeridos carregados:', data.length)),
      catchError((error) => {
        if (error.status === 500) {
          console.log('Erro 500 ao buscar sugestões - isto pode ser esperado se não houver usuário autenticado');
        } else {
          console.error('Erro inesperado ao buscar sugestões:', error);
        }
        return of([] as Material[]); 
      })
    );
  }

  filterByInterestAreas(areas: string[]): Observable<Material[]> {
    console.log('Filtrando por áreas de interesse:', areas);
    return this.http.post<Material[]>(`${this.apiUrl}/filtrar-por-areas`, areas).pipe(
      tap(data => console.log('Materiais filtrados carregados:', data.length)),
      catchError(this.handleError)
    );
  }

  createMaterial(material: Material, file?: File): Observable<Material> {
    console.log('Criando novo material:', material);
    const formData = new FormData();
    const materialBlob = new Blob([JSON.stringify(material)], { type: 'application/json' });
    
    formData.append('material', materialBlob, 'material.json');
    
    if (file) {
      console.log('Anexando arquivo:', file.name);
      formData.append('arquivo', file, file.name);
    }
    
    return this.http.post<Material>(this.apiUrl, formData).pipe(
      tap(data => console.log('Material criado com ID:', data.id)),
      catchError(this.handleError)
    );
  }

  updateMaterial(id: number, material: Material, file?: File): Observable<Material> {
    console.log(`Atualizando material com ID ${id}`);
    const formData = new FormData();
    const materialBlob = new Blob([JSON.stringify(material)], { type: 'application/json' });
    
    formData.append('material', materialBlob, 'material.json');
    
    if (file) {
      console.log('Anexando novo arquivo:', file.name);
      formData.append('arquivo', file, file.name);
    }
    
    return this.http.put<Material>(`${this.apiUrl}/${id}`, formData).pipe(
      tap(_ => console.log(`Material ${id} atualizado com sucesso`)),
      catchError(this.handleError)
    );
  }

  deleteMaterial(id: number): Observable<void> {
    console.log(`Excluindo material com ID ${id}`);
    return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(
      tap(_ => console.log(`Material ${id} excluído com sucesso`)),
      catchError(this.handleError)
    );
  }
  
  getInterestAreas(): Observable<{value: string, label: string}[]> {
    console.log('Buscando áreas de interesse da API');
    return this.http.get<{value: string, label: string}[]>(`${this.apiUrl}/areas-interesse`).pipe(
      tap(data => console.log('Áreas de interesse carregadas:', data.length)),
      catchError(error => {
        console.error('Erro ao buscar áreas de interesse:', error);
        // Retorna um array vazio em caso de erro para evitar quebrar a aplicação
        return of([]);
      })
    );
  }
}
