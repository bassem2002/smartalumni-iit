import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AlumniService } from '../services/alumni.service';

@Component({
  selector: 'app-mentors',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './mentors.component.html',
  styleUrl: './mentors.component.css',
})
export class MentorsComponent implements OnInit {
  mentors: any[] = [];
  loading = true;
  filters = {
    keyword: '',
    secteur: '',
    pays: '',
    disponibleMentorat: true
  };

  constructor(private alumniService: AlumniService) {}

  ngOnInit() {
    this.onSearch();
  }

  onSearch() {
    this.loading = true;
    this.alumniService.searchAlumni(this.filters).subscribe({
      next: (data: any) => {
        this.mentors = data.content;
        this.loading = false;
      },
      error: (err: any) => {
        console.error('Error fetching mentors', err);
        this.loading = false;
      }
    });
  }

  resetFilters() {
    this.filters = {
      keyword: '',
      secteur: '',
      pays: '',
      disponibleMentorat: true
    };
    this.onSearch();
  }

  getSkills(skillsStr: string): string[] {
    if (!skillsStr) return [];
    return skillsStr.split(',').map(s => s.trim()).filter(s => s.length > 0).slice(0, 3);
  }
}
