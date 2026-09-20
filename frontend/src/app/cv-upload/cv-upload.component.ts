import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { CvService, GestionnaireCVResponse } from '../services/cv.service';

@Component({
  selector: 'app-cv-upload',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './cv-upload.component.html',
  styleUrls: ['./cv-upload.component.css']
})
export class CvUploadComponent implements OnInit {
  cv: GestionnaireCVResponse | null = null;
  loading = false;
  uploading = false;
  analyzing = false;
  validating = false;
  deleting = false;

  selectedFile: File | null = null;
  showSuccessPopup = false;
  showDeleteConfirm = false;
  successMessage: string | null = null;
  errorMessage: string | null = null;

  editData: {
    posteActuel: string;
    entreprise: string;
    secteur: string;
    pays: string;
    competences: string;
    experiences: string;
    formations: string;
    certifications: string;
  } = {
    posteActuel: '', entreprise: '', secteur: '', pays: '',
    competences: '', experiences: '', formations: '', certifications: ''
  };

  constructor(private cvService: CvService, private router: Router) {}

  ngOnInit(): void {
    this.loadCV();
  }

  loadCV(): void {
    this.loading = true;
    this.errorMessage = null;
    this.cvService.getMyCV().subscribe({
      next: (data) => {
        this.cv = data;
        this.populateEditData();
        this.loading = false;
      },
      error: (err) => {
        this.cv = null;
        if (err.status !== 404) {
          this.errorMessage = 'Erreur lors du chargement du CV.';
        }
        this.loading = false;
      }
    });
  }

  populateEditData(): void {
    if (!this.cv) return;
    this.editData = {
      posteActuel: this.cv.posteDetecte || '',
      entreprise: this.cv.entrepriseDetectee || '',
      secteur: this.cv.secteurDetecte || '',
      pays: this.cv.paysDetecte || '',
      competences: this.toEditString(this.cv.competencesDetectees, ', '),
      experiences: this.toEditString(this.cv.experiencesDetectees, '\n'),
      formations: this.toEditString(this.cv.formationsDetectees, '\n'),
      certifications: this.toEditString(this.cv.certificationsDetectees, '\n')
    };
  }

  private toEditString(value: string[] | string | undefined, sep: string): string {
    if (!value) return '';
    if (Array.isArray(value)) return value.filter(v => v?.trim()).join(sep);
    return value;
  }

  onFileSelected(event: any): void {
    this.selectedFile = event.target.files[0] || null;
  }

  uploadCV(): void {
    if (!this.selectedFile) return;
    this.uploading = true;
    this.errorMessage = null;
    this.cvService.uploadCV(this.selectedFile).subscribe({
      next: (data) => {
        this.cv = data;
        this.selectedFile = null;
        this.populateEditData();
        this.uploading = false;
        this.showAlert('CV uploadé avec succès. Cliquez sur "Analyser" pour extraire les données.');
      },
      error: (err) => {
        this.errorMessage = err?.error?.message || 'Erreur lors du téléversement. Vérifiez le format du fichier (PDF, DOC, DOCX).';
        this.uploading = false;
      }
    });
  }

  analyzeCV(): void {
    const cvId = this.cvService.getCurrentCvId(this.cv);
    if (!cvId) { this.errorMessage = 'ID du CV introuvable.'; return; }
    this.analyzing = true;
    this.errorMessage = null;
    this.cvService.analyzeCV(cvId).subscribe({
      next: (data) => {
        this.cv = data;
        this.populateEditData();
        this.analyzing = false;
        this.showAlert('Analyse terminée ! Vérifiez les données ci-dessous avant de valider.');
      },
      error: (err) => {
        this.errorMessage = err?.error?.message || 'Erreur lors de l\'analyse du CV.';
        this.analyzing = false;
      }
    });
  }

  validateAndUpdateProfile(): void {
    if (this.validating) return; // Guard anti-double-appel

    const cvId = this.cvService.getCurrentCvId(this.cv);
    if (!cvId) { this.errorMessage = 'ID du CV introuvable.'; return; }
    this.validating = true;
    this.errorMessage = null;

    // Priorité : valeur saisie par l'utilisateur, sinon fallback sur les données du CV
    const f = (edited: string, fallback?: string | null) =>
      (edited?.trim() || fallback?.trim() || null);

    const validationData: Record<string, string | null> = {
      competences:    f(this.editData.competences,    this.toEditString(this.cv?.competencesDetectees, ', ')),
      experiences:    f(this.editData.experiences,    this.toEditString(this.cv?.experiencesDetectees, '\n')),
      posteActuel:    f(this.editData.posteActuel,    this.cv?.posteDetecte),
      entreprise:     f(this.editData.entreprise,     this.cv?.entrepriseDetectee),
      secteur:        f(this.editData.secteur,        this.cv?.secteurDetecte),
      pays:           f(this.editData.pays,           this.cv?.paysDetecte),
      formations:     f(this.editData.formations,     this.toEditString(this.cv?.formationsDetectees, '\n')),
      certifications: f(this.editData.certifications, this.toEditString(this.cv?.certificationsDetectees, '\n')),
    };

    this.cvService.mapperProfil(cvId, validationData).subscribe({
      next: () => {
        this.validating = false;
        this.showSuccessPopup = true; // popup → bouton "Voir mon profil" → navigate('/profile')
      },
      error: (err) => {
        this.errorMessage = err?.error?.message || 'Erreur lors de la validation du profil.';
        this.validating = false;
      }
    });
  }

  confirmDelete(): void {
    this.showDeleteConfirm = true;
  }

  cancelDelete(): void {
    this.showDeleteConfirm = false;
  }

  deleteCV(): void {
    const cvId = this.cvService.getCurrentCvId(this.cv);
    if (!cvId) return;
    this.deleting = true;
    this.cvService.deleteCV(cvId).subscribe({
      next: () => {
        this.cv = null;
        this.editData = { posteActuel: '', entreprise: '', secteur: '', pays: '', competences: '', experiences: '', formations: '', certifications: '' };
        this.showDeleteConfirm = false;
        this.deleting = false;
        this.showAlert('CV supprimé avec succès.');
      },
      error: (err) => {
        this.errorMessage = err?.error?.message || 'Erreur lors de la suppression.';
        this.deleting = false;
        this.showDeleteConfirm = false;
      }
    });
  }

  cancelReplace(): void {
    this.selectedFile = null;
  }

  onSuccessConfirm(): void {
    this.showSuccessPopup = false;
    this.router.navigate(['/profile']);
  }

  hasAnalysisData(): boolean {
    if (!this.cv) return false;
    const hasField = (v: any) => Array.isArray(v) ? v.length > 0 : !!v;
    return hasField(this.cv.posteDetecte) ||
           hasField(this.cv.entrepriseDetectee) ||
           hasField(this.cv.secteurDetecte) ||
           hasField(this.cv.paysDetecte) ||
           hasField(this.cv.competencesDetectees) ||
           hasField(this.cv.experiencesDetectees) ||
           hasField(this.cv.formationsDetectees) ||
           hasField(this.cv.certificationsDetectees);
  }

  formatDate(date: any): string {
    if (!date) return 'Inconnue';
    if (Array.isArray(date)) {
      const [year, month, day, hour = 0, min = 0] = date;
      return new Date(year, month - 1, day, hour, min)
        .toLocaleDateString('fr-FR', { day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit' });
    }
    return new Date(date).toLocaleDateString('fr-FR', { day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit' });
  }

  private showAlert(msg: string): void {
    this.successMessage = msg;
    setTimeout(() => this.successMessage = null, 4000);
  }
}
