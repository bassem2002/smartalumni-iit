import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ChatService {
  private apiUrl = 'http://localhost:8081/api/conversations';

  constructor(private http: HttpClient) {}

  getConversations(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl);
  }

  getMessages(conversationId: number, page: number = 0, size: number = 50): Observable<any> {
    return this.http.get(`${this.apiUrl}/${conversationId}/messages?page=${page}&size=${size}`);
  }

  sendMessage(conversationId: number, content: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/${conversationId}/messages`, { contenu: content });
  }

  sendFileMessage(conversationId: number, file: File, type: string, content: string = ''): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('type', type);
    formData.append('contenu', content);
    return this.http.post(`${this.apiUrl}/${conversationId}/messages/files`, formData);
  }

  markAsRead(conversationId: number): Observable<any> {
    return this.http.patch(`${this.apiUrl}/${conversationId}/read`, {});
  }
}
