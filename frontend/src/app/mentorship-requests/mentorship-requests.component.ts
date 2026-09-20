import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MentorshipService } from '../services/mentorship.service';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-mentorship-requests',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './mentorship-requests.component.html',
  styleUrl: './mentorship-requests.component.css',
})
export class MentorshipRequestsComponent implements OnInit {
  requests: any[] = [];
  loading = true;
  userType: string | null = null;
  filterStatus = 'TOUTES';

  constructor(
    private mentorshipService: MentorshipService,
    private authService: AuthService
  ) {}

  ngOnInit() {
    this.userType = this.authService.getTypeProfil();
    this.loadRequests();
  }

  get filteredRequests() {
    if (this.filterStatus === 'TOUTES') return this.requests;
    return this.requests.filter(r => r.statut === this.filterStatus);
  }

  get stats() {
    return {
      total: this.requests.length,
      enAttente: this.requests.filter(r => r.statut === 'EN_ATTENTE').length,
      acceptees: this.requests.filter(r => r.statut === 'ACCEPTEE').length,
      refusees: this.requests.filter(r => r.statut === 'REFUSEE').length
    };
  }

  setFilter(status: string) {
    this.filterStatus = status;
  }

  loadRequests() {
    this.loading = true;
    this.mentorshipService.getMyRequests().subscribe({
      next: (data: any) => {
        this.requests = data;
        this.loading = false;
      },
      error: (err: any) => {
        console.error('Error fetching requests', err);
        this.loading = false;
      }
    });
  }

  updateStatus(requestId: number, status: string) {
    this.mentorshipService.updateRequestStatus(requestId, status).subscribe({
      next: () => {
        this.loadRequests();
      },
      error: (err) => {
        console.error('Error updating status', err);
      }
    });
  }
}
