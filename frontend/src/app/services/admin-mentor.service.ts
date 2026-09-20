import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AdminMentorService {
  private apiUrl = 'http://localhost:8081/api';

  constructor(private http: HttpClient) {}

  getUsers(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/admin/users`);
  }

  getMentors(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/admin/mentors`);
  }

  getPendingMentorRequests(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/admin/demandes-mentor`);
  }

  treatMentorRequest(alumniId: number, approuver: boolean): Observable<any> {
    return this.http.patch(`${this.apiUrl}/admin/demandes-mentor/${alumniId}?approuver=${approuver}`, {});
  }

  submitMentorRequest(): Observable<any> {
    return this.http.post(`${this.apiUrl}/profils/me/demande-mentor`, {});
  }

  getStats(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/admin/stats`);
  }
}
