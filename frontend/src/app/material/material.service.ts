import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Material } from './material-module';
import { InterestArea } from './material-module';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class MaterialService {
  private apiUrl = `${environment.apiUrl}/materials`;

  constructor(private http: HttpClient) { }

  getAllMaterials(): Observable<Material[]> {
    return this.http.get<Material[]>(this.apiUrl);
  }

  getSuggestedMaterials(): Observable<Material[]> {
    return this.http.get<Material[]>(`${this.apiUrl}/sugestoes`);
  }

  createMaterial(material: Material, file?: File): Observable<Material> {
    const formData = new FormData();
    formData.append('material', new Blob([JSON.stringify(material)], { type: 'application/json' }));
    if (file) {
      formData.append('arquivo', file, file.name);
    }
    return this.http.post<Material>(this.apiUrl, formData);
  }

  // >>> NOVO MÉTODO: Atualiza um material existente
  updateMaterial(id: number, material: Material, file?: File): Observable<Material> {
    const formData = new FormData();
    formData.append('material', new Blob([JSON.stringify(material)], { type: 'application/json' }));
    if (file) {
      formData.append('arquivo', file, file.name);
    }
    return this.http.put<Material>(`${this.apiUrl}/${id}`, formData);
  }

  deleteMaterial(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  filterMaterialsByInterestAreas(areas: InterestArea[]): Observable<Material[]> {
    return this.http.post<Material[]>(`${this.apiUrl}/filtrar-por-areas`, areas);
  }

  // >>> NOVO MÉTODO: Gera a URL de download para um arquivo
  getDownloadUrl(filePath: string): string {
    // O backend serve a pasta 'upload' estaticamente.
    // Ajuste a URL base se for diferente.
    return `${environment.baseApiUrl}/${filePath}`;
  }
}