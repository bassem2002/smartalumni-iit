import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class MentorshipService {
  private apiUrl = 'http://localhost:8081/api/mentorat';

  constructor(private http: HttpClient) {}

  sendRequest(alumniId: number, message: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/demandes`, { alumniId, message });
  }

  getMyRequests(): Observable<any> {
    return this.http.get(`${this.apiUrl}/demandes`);
  }

  updateRequestStatus(requestId: number, status: string): Observable<any> {
    return this.http.patch(`${this.apiUrl}/demandes/${requestId}/statut`, { statut: status });
  }
}
