import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface GestionnaireCVResponse {
  id?: number;
  idCV?: number;
  cvId?: number;
  nomFichier: string;
  cheminStockage?: string;
  dateDepot?: string | number[];
  contenuExtrait?: boolean;
  posteDetecte?: string;
  entrepriseDetectee?: string;
  secteurDetecte?: string;
  paysDetecte?: string;
  competencesDetectees?: string[] | string;
  experiencesDetectees?: string[] | string;
  formationsDetectees?: string[] | string;
  certificationsDetectees?: string[] | string;
}

@Injectable({
  providedIn: 'root'
})
export class CvService {
  private apiUrl = 'http://localhost:8081/api/cv';

  constructor(private http: HttpClient) {}

  uploadCV(file: File): Observable<GestionnaireCVResponse> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<GestionnaireCVResponse>(`${this.apiUrl}/upload`, formData);
  }

  getMyCV(): Observable<GestionnaireCVResponse> {
    return this.http.get<GestionnaireCVResponse>(`${this.apiUrl}/me`);
  }

  getCV(cvId: number): Observable<GestionnaireCVResponse> {
    return this.http.get<GestionnaireCVResponse>(`${this.apiUrl}/${cvId}`);
  }

  analyzeCV(cvId: number): Observable<GestionnaireCVResponse> {
    return this.http.post<GestionnaireCVResponse>(`${this.apiUrl}/${cvId}/analyze`, {});
  }

  mapperProfil(cvId: number, validationData?: Record<string, string | null>): Observable<GestionnaireCVResponse> {
    return this.http.patch<GestionnaireCVResponse>(`${this.apiUrl}/${cvId}/mapper-profil`, validationData ?? {});
  }

  deleteCV(cvId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${cvId}`);
  }

  getCurrentCvId(cv: GestionnaireCVResponse | null): number | null {
    const raw: any = cv;
    return raw?.idCV ?? raw?.idCv ?? raw?.id ?? raw?.cvId ?? null;
  }
}
