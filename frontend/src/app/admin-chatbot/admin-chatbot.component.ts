import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-admin-chatbot',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-chatbot.component.html',
  styleUrl: './admin-chatbot.component.css'
})
export class AdminChatbotComponent {
  question: string = '';
  messages: {role: string, text: string}[] = [];
  loading: boolean = false;

  constructor(private http: HttpClient) {}

  ask() {
    if (!this.question.trim()) return;
    
    this.messages.push({ role: 'admin', text: this.question });
    const currentQ = this.question;
    this.question = '';
    this.loading = true;

    this.http.post<any>('http://localhost:8081/api/admin/chat/ask', { question: currentQ }).subscribe({
      next: (res) => {
        this.messages.push({ role: 'ai', text: res.reponse });
        this.loading = false;
      },
      error: (err) => {
        this.messages.push({ role: 'ai', text: "Erreur de connexion avec l'IA." });
        this.loading = false;
        console.error(err);
      }
    });
  }
}
