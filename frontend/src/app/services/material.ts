import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Material } from '../entity/material';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class MaterialService {
  private apiUrl = `${environment.apiUrl}/materiais`;

  constructor(private http: HttpClient) { }

  getAllMaterials(): Observable<Material[]> {
    return this.http.get<Material[]>(this.apiUrl);
  }

  getMaterialById(id: number): Observable<Material> {
    return this.http.get<Material>(`${this.apiUrl}/${id}`);
  }

  getSuggestedMaterials(): Observable<Material[]> {
    return this.http.get<Material[]>(`${this.apiUrl}/sugestoes`);
  }

  filterByInterestAreas(areas: string[]): Observable<Material[]> {

    return this.http.post<Material[]>(`${this.apiUrl}/filtrar-por-areas`, areas);
  }

  createMaterial(material: Material, file?: File): Observable<Material> {
    const formData = new FormData();
    const materialBlob = new Blob([JSON.stringify(material)], { type: 'application/json' });
    
    formData.append('material', materialBlob, 'material.json');
    
    if (file) {
      formData.append('arquivo', file, file.name);
    }
    
    return this.http.post<Material>(this.apiUrl, formData);
  }

  updateMaterial(id: number, material: Material, file?: File): Observable<Material> {
    const formData = new FormData();
    const materialBlob = new Blob([JSON.stringify(material)], { type: 'application/json' });
    
    formData.append('material', materialBlob, 'material.json');
    
    if (file) {
      formData.append('arquivo', file, file.name);
    }
    
    return this.http.put<Material>(`${this.apiUrl}/${id}`, formData);
  }

  deleteMaterial(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
