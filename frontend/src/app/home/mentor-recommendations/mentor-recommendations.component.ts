import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { RecommandationService, RecommandationIAResponse } from '../../services/recommandation.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-mentor-recommendations',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './mentor-recommendations.component.html',
  styleUrl: './mentor-recommendations.component.css'
})
export class MentorRecommendationsComponent implements OnInit {
  recommendations: RecommandationIAResponse[] = [];
  loading = false;
  generating = false;
  error: string | null = null;

  constructor(
    private recommandationService: RecommandationService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    if (this.authService.isAuthenticated() && !this.authService.isAdmin()) {
      this.loadRecommendations();
    }
  }

  loadRecommendations(): void {
    this.loading = true;
    this.error = null;
    this.recommandationService.getMyRecommendations().subscribe({
      next: (data) => {
        // Sort by score descending and take only the first 4
        this.recommendations = data
          .sort((a, b) => b.scoreMatching - a.scoreMatching)
          .slice(0, 4);
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading recommendations', err);
        this.error = 'Impossible de charger les recommandations. Veuillez réessayer plus tard.';
        this.loading = false;
      }
    });
  }

  generate(): void {
    this.generating = true;
    this.error = null;
    this.recommandationService.generateRecommendations().subscribe({
      next: (data) => {
        this.recommendations = data
          .sort((a, b) => b.scoreMatching - a.scoreMatching)
          .slice(0, 4);
        this.generating = false;
      },
      error: (err) => {
        console.error('Error generating recommendations', err);
        this.error = 'Erreur lors de la génération des recommandations. Vérifiez que votre profil est complet.';
        this.generating = false;
      }
    });
  }

  getInitials(rec: RecommandationIAResponse): string {
    return `${rec.alumniPrenom[0]}${rec.alumniNom[0]}`.toUpperCase();
  }
}
