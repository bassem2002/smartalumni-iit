import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { MentorRecommendationsComponent } from './mentor-recommendations/mentor-recommendations.component';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterModule, MentorRecommendationsComponent],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit {
  isAuthenticated = false;
  isStudent = false;

  constructor(public authService: AuthService, private router: Router) {}

  ngOnInit() {
    this.isAuthenticated = !!this.authService.getToken();
    if (this.isAuthenticated) {
      if (this.authService.isAdmin()) {
        this.router.navigate(['/admin/dashboard']);
      } else {
        // Assume non-admin users can see recommendations (Student/Alumni)
        // The prompt says "espace étudiant", let's check the role specifically if possible
        this.isStudent = this.authService.getRole() === 'ROLE_ETUDIANT' || !this.authService.isAlumni();
      }
    }
  }

  logout() {
    this.authService.logout();
    this.isAuthenticated = false;
    this.router.navigate(['/login']);
  }
}
