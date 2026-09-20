import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8081/api/auth';

  constructor(private http: HttpClient) {}

  register(userData: any, cvFile: File): Observable<any> {
    const formData = new FormData();
    formData.append('data', new Blob([JSON.stringify(userData)], { type: 'application/json' }));
    formData.append('cv', cvFile);
    return this.http.post(`${this.apiUrl}/register`, formData);
  }

  getFormations(): Observable<{ [key: string]: string[] }> {
    return this.http.get<{ [key: string]: string[] }>('http://localhost:8081/api/formations');
  }

  login(credentials: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/login`, credentials);
  }

  saveToken(token: string): void {
    localStorage.setItem('auth_token', token);
  }

  getToken(): string | null {
    return localStorage.getItem('auth_token');
  }

  saveUserInfo(data: any): void {
    localStorage.setItem('user_info', JSON.stringify(data));
  }

  getUserInfo(): any {
    const info = localStorage.getItem('user_info');
    return info ? JSON.parse(info) : null;
  }

  getTypeProfil(): string | null {
    return this.getUserInfo()?.typeProfil ?? null;
  }

  getRole(): string | null {
    return this.getUserInfo()?.role ?? null;
  }

  isAdmin(): boolean {
    return this.getRole() === 'ROLE_ADMIN';
  }

  isAlumni(): boolean {
    return this.getTypeProfil() === 'ALUMNI';
  }

  isStudent(): boolean {
    return this.getTypeProfil() === 'ETUDIANT';
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  logout(): void {
    localStorage.removeItem('auth_token');
    localStorage.removeItem('user_info');
  }
}
