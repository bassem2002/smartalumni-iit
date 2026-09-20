import { Component, OnInit, OnDestroy, ViewChild, ElementRef, AfterViewChecked } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { Subscription, interval } from 'rxjs';
import { ChatService } from '../services/chat.service';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './chat.component.html',
  styleUrl: './chat.component.css'
})
export class ChatComponent implements OnInit, OnDestroy, AfterViewChecked {
  @ViewChild('scrollMe') private myScrollContainer!: ElementRef;

  conversations: any[] = [];
  selectedConversation: any = null;
  messages: any[] = [];
  newMessage = '';
  loadingConversations = true;
  loadingMessages = false;
  currentUserId: number | null = null;
  targetMentorId: number | null = null;
  private targetMentorOpened = false;

  isRecording = false;
  private mediaRecorder?: MediaRecorder;
  private audioChunks: BlobPart[] = [];
  private pollingSubscription?: Subscription;

  constructor(
    private chatService: ChatService,
    private authService: AuthService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    const userInfo = this.authService.getUserInfo();
    this.currentUserId = userInfo ? Number(userInfo.userId) : null;
  }

  ngOnInit(): void {
    this.route.queryParamMap.subscribe(params => {
      const mentorId = params.get('mentorId');
      this.targetMentorId = mentorId ? Number(mentorId) : null;
      this.targetMentorOpened = false;
      this.tryOpenMentorConversation();
    });

    this.loadConversations();
    this.route.params.subscribe(params => {
      if (params['id']) {
        this.selectConversationById(+params['id']);
      }
    });
    this.startPolling();
  }

  ngOnDestroy(): void {
    this.pollingSubscription?.unsubscribe();
  }

  ngAfterViewChecked(): void {
    this.scrollToBottom();
  }

  scrollToBottom(): void {
    try {
      this.myScrollContainer.nativeElement.scrollTop = this.myScrollContainer.nativeElement.scrollHeight;
    } catch (err) {}
  }

  startPolling(): void {
    this.pollingSubscription = interval(5000).subscribe(() => {
      if (this.selectedConversation) {
        this.loadMessages(this.selectedConversation.id, false);
      }
      this.loadConversations(false);
    });
  }

  loadConversations(showLoading = true): void {
    if (showLoading) this.loadingConversations = true;
    this.chatService.getConversations().subscribe({
      next: (data) => {
        this.conversations = data;
        this.loadingConversations = false;
        this.tryOpenMentorConversation();

        const routeId = this.route.snapshot.params['id'];
        if (routeId && (!this.selectedConversation || this.selectedConversation.id !== +routeId)) {
          this.selectConversationById(+routeId);
        }
      },
      error: (err) => {
        console.error('Error loading conversations', err);
        this.loadingConversations = false;
      }
    });
  }

  selectConversationById(id: number): void {
    if (this.conversations.length === 0) return;
    const conversation = this.conversations.find(c => c.id === id);
    if (conversation) {
      this.selectedConversation = conversation;
      this.loadMessages(id);
      this.chatService.markAsRead(id).subscribe();
    }
  }

  onSelectConversation(conv: any): void {
    this.router.navigate(['/conversations', conv.id]);
  }

  private tryOpenMentorConversation(): void {
    if (!this.targetMentorId || this.targetMentorOpened || this.conversations.length === 0) {
      return;
    }

    const conversation = this.conversations.find(conv => Number(conv.alumniId) === this.targetMentorId);
    if (!conversation) {
      return;
    }

    this.targetMentorOpened = true;
    this.router.navigate(['/conversations', conversation.id], { replaceUrl: true });
  }

  loadMessages(id: number, showLoading = true): void {
    if (showLoading) this.loadingMessages = true;
    this.chatService.getMessages(id).subscribe({
      next: (data) => {
        this.messages = data.content;
        this.messages.sort((a: any, b: any) => new Date(a.dateEnvoi).getTime() - new Date(b.dateEnvoi).getTime());
        this.loadingMessages = false;
        setTimeout(() => this.scrollToBottom(), 100);
      },
      error: (err) => {
        console.error('Error loading messages', err);
        this.loadingMessages = false;
      }
    });
  }

  sendMessage(): void {
    if (!this.newMessage.trim() || !this.selectedConversation) return;
    const content = this.newMessage;
    this.newMessage = '';
    this.chatService.sendMessage(this.selectedConversation.id, content).subscribe({
      next: (message) => {
        this.messages.push(message);
        this.scrollToBottom();
      },
      error: (err) => console.error('Error sending message', err)
    });
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file || !this.selectedConversation) return;

    let type = 'FILE';
    if (file.type.startsWith('image/')) type = 'IMAGE';
    if (file.type.startsWith('audio/')) type = 'AUDIO';

    this.chatService.sendFileMessage(this.selectedConversation.id, file, type).subscribe({
      next: (message) => {
        this.messages.push(message);
        this.scrollToBottom();
      },
      error: (err) => console.error('Error uploading file', err)
    });
  }

  toggleRecording(): void {
    if (this.isRecording) {
      this.stopRecording();
    } else {
      this.startRecording();
    }
  }

  async startRecording(): Promise<void> {
    try {
      const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
      const mimeType = MediaRecorder.isTypeSupported('audio/webm') ? 'audio/webm' : 'audio/ogg';

      this.mediaRecorder = new MediaRecorder(stream, { mimeType });
      this.audioChunks = [];

      this.mediaRecorder.ondataavailable = (event) => {
        if (event.data.size > 0) this.audioChunks.push(event.data);
      };

      this.mediaRecorder.onstop = () => {
        const audioBlob = new Blob(this.audioChunks, { type: mimeType });
        const extension = mimeType.split('/')[1];
        const audioFile = new File([audioBlob], `vocal_message.${extension}`, { type: mimeType });
        this.sendFileMessage(audioFile, 'AUDIO');
        stream.getTracks().forEach(track => track.stop());
      };

      this.mediaRecorder.start();
      this.isRecording = true;
    } catch (err) {
      console.error('Could not start recording', err);
      alert('Microphone error: Please ensure you have granted permission and are using a modern browser.');
    }
  }

  stopRecording(): void {
    if (this.mediaRecorder && this.isRecording) {
      this.mediaRecorder.stop();
      this.isRecording = false;
    }
  }

  sendFileMessage(file: File, type: string): void {
    if (!this.selectedConversation) return;
    this.chatService.sendFileMessage(this.selectedConversation.id, file, type).subscribe({
      next: (message) => {
        this.messages.push(message);
        this.scrollToBottom();
      },
      error: (err) => console.error('Error sending file/vocal', err)
    });
  }

  getOtherParticipantName(conv: any): string {
    const isAlumni = this.authService.isAlumni();
    return isAlumni ? `${conv.etudiantPrenom} ${conv.etudiantNom}` : `${conv.alumniPrenom} ${conv.alumniNom}`;
  }

  getOtherParticipantPhoto(conv: any): string | null {
    const isAlumni = this.authService.isAlumni();
    return isAlumni ? conv.etudiantPhoto : conv.alumniPhoto;
  }

  getRandomColor(name: string): string {
    const colors = ['#f87171', '#fb923c', '#fbbf24', '#34d399', '#22d3ee', '#60a5fa', '#818cf8', '#a78bfa', '#f472b6'];
    let hash = 0;
    for (let i = 0; i < name.length; i++) {
      hash = name.charCodeAt(i) + ((hash << 5) - hash);
    }
    return colors[Math.abs(hash) % colors.length];
  }

  getOtherParticipantRole(conv: any): string {
    const isAlumni = this.authService.isAlumni();
    return isAlumni ? 'Étudiant' : 'Alumni';
  }

  getFileUrl(fileUrl: string): string {
    if (!fileUrl) return '';
    if (fileUrl.startsWith('http')) return fileUrl;
    return `http://localhost:8081${fileUrl}`;
  }

  getFileName(fileUrl: string): string {
    if (!fileUrl) return '';
    const parts = fileUrl.split('/');
    const fullName = parts[parts.length - 1];
    const underscoreIndex = fullName.indexOf('_');
    if (underscoreIndex !== -1) {
      return fullName.substring(underscoreIndex + 1);
    }
    return fullName;
  }
}
