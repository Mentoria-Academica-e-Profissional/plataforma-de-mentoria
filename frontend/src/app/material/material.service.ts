import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Material, InterestArea } from './material-module';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class MaterialService {
  private apiUrl = `${environment.apiUrl}/materials`;

  constructor(private http: HttpClient) { }

  private getAuthHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
  }

  private getAuthHeadersForFormData(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
  }

  getAllMaterials(): Observable<Material[]> {
    const headers = this.getAuthHeaders();
    return this.http.get<Material[]>(this.apiUrl, { headers });
  }

  getSuggestedMaterials(): Observable<Material[]> {
    const headers = this.getAuthHeaders();
    return this.http.get<Material[]>(`${this.apiUrl}/sugestoes`, { headers });
  }

  createMaterial(material: Material, file?: File): Observable<Material> {
    const headers = this.getAuthHeadersForFormData(); // Obtém os headers
    const formData = new FormData();
    formData.append('title', material.title);
    formData.append('materialType', material.materialType);
    if (material.url) {
      formData.append('url', material.url);
    }
    material.interestArea.forEach(area => formData.append('interestArea', area));
    
    if (file) {
      const sanitizedFileName = file.name.replace(/.*[\/\\]/, '');
      console.log('Nome do arquivo enviado para o backend:', sanitizedFileName); 
      formData.append('arquivo', file, sanitizedFileName);
      console.log('Nome do arquivo enviado para o backend:', sanitizedFileName); 
    }
    
    // Adiciona os headers na requisição POST
    return this.http.post<Material>(this.apiUrl, formData, { headers });
  }

  updateMaterial(id: number, material: Material, file?: File): Observable<Material> {
    const headers = this.getAuthHeadersForFormData(); // Obtém os headers
    const formData = new FormData();
    formData.append('title', material.title);
    formData.append('materialType', material.materialType);
    if (material.url) {
      formData.append('url', material.url);
    }
    material.interestArea.forEach(area => formData.append('interestArea', area));

    if (file) {
      const sanitizedFileName = file.name.replace(/.*[\/\\]/, '');
      formData.append('arquivo', file, sanitizedFileName);
    }
    
    // Adiciona os headers na requisição PUT
    return this.http.put<Material>(`${this.apiUrl}/${id}`, formData, { headers });
  }

  deleteMaterial(id: number): Observable<void> {
    const headers = this.getAuthHeaders();
    return this.http.delete<void>(`${this.apiUrl}/${id}`, { headers });
  }

  filterMaterialsByInterestAreas(areas: InterestArea[]): Observable<Material[]> {
    const headers = this.getAuthHeaders();
    return this.http.post<Material[]>(`${this.apiUrl}/filtrar-por-areas`, areas, { headers });
  }
  
  getDownloadUrl(filePath: string): string {
    const fileName = filePath.split('/').pop() || filePath;
    const apiBase = environment.apiUrl.replace('/api', ''); 
    return `${apiBase}/upload/${fileName}`;
  }
}