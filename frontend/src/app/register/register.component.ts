import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css',
})
export class RegisterComponent implements OnInit {
  userData: any = {
    nom: '',
    prenom: '',
    email: '',
    motDePasse: '',
    typeProfil: '',
    filiere: '',
    specialite: '',
    niveauEtude: '',
    anneePromotion: null,
    secteur: '',
    posteActuel: '',
    entreprise: '',
    pays: ''
  };
  confirmPassword = '';
  termsAccepted = false;
  error: string | null = null;
  cvFile: File | null = null;
  cvFileName = '';

  // Dynamic IIT filières and their specialties fetched from backend
  filiereOptions: { [key: string]: string[] } = {};

  get filieres(): string[] {
    return Object.keys(this.filiereOptions);
  }

  get specialites(): string[] {
    if (!this.userData.filiere) return [];
    return this.filiereOptions[this.userData.filiere] || [];
  }

  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit(): void {
    this.authService.getFormations().subscribe({
      next: (data) => {
        this.filiereOptions = data;
      },
      error: (err) => {
        console.error('Erreur lors du chargement des formations', err);
      }
    });
  }

  onFiliereChange(): void {
    this.userData.specialite = '';
  }

  onCvSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files[0]) {
      this.cvFile = input.files[0];
      this.cvFileName = this.cvFile.name;
    }
  }

  isEmailValid(): boolean {
    if (!this.userData.email) return true; // Don't show error for empty
    return this.userData.email.toLowerCase().endsWith('@iit.ens.tn');
  }

  isFormValid(): boolean {
    const base = this.userData.nom && this.userData.prenom && this.userData.email
      && this.userData.motDePasse && this.userData.typeProfil
      && this.userData.filiere && this.userData.specialite
      && this.cvFile
      && this.isEmailValid()
      && this.userData.motDePasse === this.confirmPassword
      && this.termsAccepted
      && this.userData.motDePasse.length >= 8;

    if (this.userData.typeProfil === 'ALUMNI') {
      return base && this.userData.secteur && this.userData.posteActuel
        && this.userData.entreprise && this.userData.pays;
    }
    return !!base;
  }

  onSubmit() {
    this.error = null;

    if (!this.isEmailValid()) {
      this.error = "L'email doit se terminer par @iit.ens.tn";
      return;
    }

    if (!this.cvFile) {
      this.error = 'Le CV est obligatoire';
      return;
    }

    this.authService.register(this.userData, this.cvFile).subscribe({
      next: (response: any) => {
        console.log('Registration successful', response);
        this.authService.saveToken(response.token);
        this.router.navigate(['/login']);
      },
      error: (err: any) => {
        console.error('Registration failed', err);
        this.error = err.error?.message || 'Registration failed. Please try again.';
      }
    });
  }
}
