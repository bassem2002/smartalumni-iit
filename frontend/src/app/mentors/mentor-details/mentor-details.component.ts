import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AlumniService } from '../../services/alumni.service';
import { MentorshipService } from '../../services/mentorship.service';

@Component({
  selector: 'app-mentor-details',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './mentor-details.component.html',
  styleUrl: './mentor-details.component.css',
})
export class MentorDetailsComponent implements OnInit {
  mentor: any;
  loading = true;
  showRequestModal = false;
  requestMessage = '';
  sending = false;
  successMessage: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private alumniService: AlumniService,
    private mentorshipService: MentorshipService
  ) {}

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.alumniService.getAlumniDetails(+id).subscribe({
        next: (data: any) => {
          this.mentor = data;
          this.loading = false;
        },
        error: (err: any) => {
          console.error('Error fetching mentor details', err);
          this.loading = false;
        }
      });
    }
  }

  getSkills(skillsStr: string): string[] {
    if (!skillsStr) return [];
    return skillsStr.split(',').map(s => s.trim()).filter(s => s.length > 0);
  }

  openRequestModal() {
    this.showRequestModal = true;
  }

  openConversation() {
    if (!this.mentor?.id) return;
    this.router.navigate(['/conversations'], {
      queryParams: { mentorId: this.mentor.id }
    });
  }

  closeRequestModal() {
    this.showRequestModal = false;
    this.requestMessage = '';
  }

  sendRequest() {
    this.sending = true;
    this.mentorshipService.sendRequest(this.mentor.id, this.requestMessage).subscribe({
      next: () => {
        this.sending = false;
        this.closeRequestModal();
        this.showToast('Mentorship request sent successfully!');
      },
      error: (err: any) => {
        console.error('Error sending request', err);
        this.sending = false;
      }
    });
  }

  showToast(message: string) {
    this.successMessage = message;
    setTimeout(() => this.successMessage = null, 3000);
  }
}
