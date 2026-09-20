import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProfileService } from '../services/profile.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {
  profile: any = null;
  isEditing = false;
  loading = false;
  showSuccessPopup = false;
  updateError: string | null = null;
  updateSuccess: string | null = null;

  editData: any = {};

  constructor(private profileService: ProfileService) {}

  ngOnInit(): void {
    this.loadProfile();
  }

  loadProfile(): void {
    this.loading = true;
    this.profileService.getMyProfile().subscribe({
      next: (data) => {
        if (data) {
          this.profile = data;
          this.editData = { ...data };
        }
        this.loading = false;
      },
      error: (err: any) => {
        console.error('Erreur chargement profil:', err);
        this.loading = false;
      }
    });
  }

  toggleEdit(): void {
    this.isEditing = !this.isEditing;
    if (this.profile) {
      this.editData = { ...this.profile };
    }
  }

  onFileSelected(event: any): void {
    const file: File = event.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = (e: any) => {
        const base64Image = e.target.result;
        if (this.profile) {
          this.profile.photo = base64Image; // Aperçu
        }
        this.saveAvatar(base64Image);
      };
      reader.readAsDataURL(file);
    }
  }

  saveAvatar(base64: string): void {
    this.profileService.updateProfile({ photo: base64 }).subscribe({
      next: () => console.log('Avatar sauvegardé'),
      error: (err: any) => console.error('Erreur avatar:', err)
    });
  }

  saveProfile(): void {
    this.loading = true;
    this.profileService.updateProfile(this.editData).subscribe({
      next: (updated) => {
        this.profile = updated;
        this.editData = { ...updated };
        this.isEditing = false;
        this.loading = false;
        this.showSuccessPopup = true;
      },
      error: (err: any) => {
        this.loading = false;
        alert('Erreur lors de la sauvegarde.');
      }
    });
  }

  closeSuccessPopup(): void {
    this.showSuccessPopup = false;
  }

  getMentorStatusLabel(): string {
    switch (this.profile?.demandeStatut) {
      case 'EN_ATTENTE':
        return 'En attente';
      case 'ACCEPTEE':
        return 'Acceptée';
      default:
        return 'Aucune demande';
    }
  }

  getMentorStatusClass(): string {
    switch (this.profile?.demandeStatut) {
      case 'EN_ATTENTE':
        return 'EN_ATTENTE';
      case 'ACCEPTEE':
        return 'ACCEPTEE';
      default:
        return 'AUCUNE';
    }
  }

  canRequestMentor(): boolean {
    return this.profile?.typeUser === 'ALUMNI' && (!this.profile?.demandeStatut || this.profile.demandeStatut === 'AUCUNE');
  }

  demanderDevenirMentor(): void {
    this.loading = true;
    this.profileService.soumettreDemandeMentor().subscribe({
      next: () => {
        if (this.profile) this.profile.demandeStatut = 'EN_ATTENTE';
        this.loading = false;
        alert('Demande envoyée !');
      },
      error: (err: any) => {
        this.loading = false;
        alert(err?.error?.message || 'Erreur demande mentor.');
      }
    });
  }

  openPasswordModal(): void {
    alert('Changement de mot de passe bientôt disponible.');
  }
}
