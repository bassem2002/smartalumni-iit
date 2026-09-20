import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface RecommandationIAResponse {
  id: number;
  scoreMatching: number;
  raison: string;
  dateGeneration: string;
  alumniId: number;
  alumniNom: string;
  alumniPrenom: string;
  alumniPhoto: string | null;
  secteur: string | null;
  posteActuel: string | null;
  entreprise: string | null;
  pays: string | null;
  competences: string | null;
}

@Injectable({
  providedIn: 'root'
})
export class RecommandationService {
  private apiUrl = 'http://localhost:8081/api/recommandations';

  constructor(private http: HttpClient) {}

  generateRecommendations(): Observable<RecommandationIAResponse[]> {
    return this.http.post<RecommandationIAResponse[]>(`${this.apiUrl}/generer`, {});
  }

  getMyRecommendations(): Observable<RecommandationIAResponse[]> {
    return this.http.get<RecommandationIAResponse[]>(`${this.apiUrl}/me`);
  }
}
