import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AlumniService {
  private apiUrl = 'http://localhost:8081/api/alumni';

  constructor(private http: HttpClient) {}

  searchAlumni(filters: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/search`, filters || {});
  }

  getAlumniDetails(id: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/${id}`);
  }

  toggleMentorshipStatus(id: number): Observable<any> {
    return this.http.patch(`${this.apiUrl}/${id}/mentorat`, {});
  }
}
