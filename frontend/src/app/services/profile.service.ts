import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ProfileService {
  private apiUrl = 'http://localhost:8081/api/profils';

  constructor(private http: HttpClient) {}

  getMyProfile(): Observable<any> {
    return this.http.get(`${this.apiUrl}/me`);
  }

  updateProfile(profileData: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/me`, profileData);
  }

  addTitle(titleData: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/me/titres`, titleData);
  }

  deleteTitle(titleId: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/me/titres/${titleId}`);
  }

  changePassword(passwordData: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/me/password`, passwordData);
  }

  soumettreDemandeMentor(): Observable<any> {
    return this.http.post(`http://localhost:8081/api/profils/me/demande-mentor`, {});
  }
}
