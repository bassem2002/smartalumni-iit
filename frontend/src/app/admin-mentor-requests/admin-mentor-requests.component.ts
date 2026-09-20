import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminMentorService } from '../services/admin-mentor.service';

@Component({
  selector: 'app-admin-mentor-requests',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './admin-mentor-requests.component.html',
  styleUrl: './admin-mentor-requests.component.css'
})
export class AdminMentorRequestsComponent implements OnInit {
  requests: any[] = [];
  loading = true;
  error = '';
  successMessage = '';

  constructor(private adminMentorService: AdminMentorService) {}

  ngOnInit(): void {
    this.loadRequests();
  }

  loadRequests() {
    this.loading = true;
    this.adminMentorService.getPendingMentorRequests().subscribe({
      next: (data) => {
        this.requests = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Erreur lors du chargement des demandes.';
        this.loading = false;
        console.error(err);
      }
    });
  }

  treat(alumniId: number, approve: boolean) {
    this.successMessage = '';
    this.error = '';
    this.adminMentorService.treatMentorRequest(alumniId, approve).subscribe({
      next: () => {
        this.successMessage = approve
          ? 'Demande approuvée ! L\'alumni est maintenant mentor.'
          : 'Demande refusée.';
        this.requests = this.requests.filter(r => r.id !== alumniId);
      },
      error: (err) => {
        this.error = err.error?.message || 'Erreur lors du traitement.';
        console.error(err);
      }
    });
  }
}
