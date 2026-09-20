import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { ProfileService } from '../services/profile.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})
export class NavbarComponent implements OnInit {
  isAuthenticated = false;
  userPhoto: string | null = null;
  isMentor = false;
  isMenuOpen = false;

  constructor(
    public authService: AuthService,
    private router: Router,
    private profileService: ProfileService
  ) { }

  ngOnInit() {
    this.checkAuth();
    this.router.events.subscribe(() => {
      this.checkAuth();
      this.isMenuOpen = false; // Close menu on navigation
    });
  }

  checkAuth() {
    this.isAuthenticated = this.authService.isAuthenticated();
    if (this.isAuthenticated && !this.authService.isAdmin()) {
      this.loadUserPhoto();
    }
  }

  loadUserPhoto() {
    this.profileService.getMyProfile().subscribe({
      next: (data) => {
        this.userPhoto = data.photo;
        this.isMentor = (data.demandeStatut === 'ACCEPTEE' || data.disponibleMentorat === true);
      },
      error: () => {
        this.userPhoto = null;
        this.isMentor = false;
      }
    });
  }

  toggleMenu() {
    this.isMenuOpen = !this.isMenuOpen;
  }

  shouldShowNavbar(): boolean {
    return true; // Visible everywhere for now as requested for modern layout
  }

  logout() {
    this.authService.logout();
    this.isAuthenticated = false;
    this.userPhoto = null;
    this.isMentor = false;
    this.router.navigate(['/home']);
  }
}
