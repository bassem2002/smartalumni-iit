import { Component, OnInit, AfterViewInit, ElementRef, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminMentorService } from '../services/admin-mentor.service';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';
import { AdminChatbotComponent } from '../admin-chatbot/admin-chatbot.component';
import Chart from 'chart.js/auto';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, AdminChatbotComponent],
  templateUrl: './admin-dashboard.component.html',
  styleUrl: './admin-dashboard.component.css'
})
export class AdminDashboardComponent implements OnInit, AfterViewInit {
  @ViewChild('myChart') chartCanvas!: ElementRef;
  chartInstance: any = null;
  
  stats: any = null;
  users: any[] = [];
  mentors: any[] = [];
  requests: any[] = [];
  loadingStats = true;
  loadingUsers = true;
  loadingMentors = true;
  loadingRequests = true;
  error = '';
  successMessage = '';
  
  searchQuery = '';
  searchAttribute = 'all';
  filterRole = '';

  activeTab: 'overview' | 'users' | 'mentors' | 'requests' = 'overview';

  constructor(
    private adminService: AdminMentorService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadStats();
    this.loadUsers();
    this.loadMentors();
    this.loadRequests();
  }

  setTab(tab: 'overview' | 'users' | 'mentors' | 'requests') {
    this.activeTab = tab;
    if (tab === 'overview') {
      setTimeout(() => this.updateChart(), 100);
    }
  }

  getBadgeClass(role: string): string {
    const r = role.toLowerCase();
    if (r.includes('admin')) return 'badge-danger';
    if (r.includes('alumni')) return 'badge-primary';
    if (r.includes('etudiant')) return 'badge-success';
    return 'badge-soft';
  }

  ngAfterViewInit() {
    this.initChart();
  }

  initChart() {
    this.chartInstance = new Chart(this.chartCanvas.nativeElement, {
      type: 'doughnut',
      data: {
        labels: [],
        datasets: [{
          label: 'Alumni',
          data: [],
          backgroundColor: ['#4facfe', '#00f2fe', '#43e97b', '#38f9d7', '#667eea', '#764ba2', '#ff0844', '#ffb199'],
          borderWidth: 0
        }]
      },
      options: { 
        responsive: true, 
        plugins: { 
          legend: { 
            position: 'bottom',
            labels: { color: 'white' }
          } 
        } 
      }
    });
    this.updateChart();
  }

  updateChart() {
    if (this.chartInstance && this.stats && this.stats.alumniParSecteur) {
      const secteurs = this.stats.alumniParSecteur;
      const labels = Object.keys(secteurs);
      const data = Object.values(secteurs);
      
      if (labels.length === 0) {
        this.chartInstance.data.labels = ['Aucune donnée'];
        this.chartInstance.data.datasets[0].data = [1];
      } else {
        this.chartInstance.data.labels = labels;
        this.chartInstance.data.datasets[0].data = data;
      }
      this.chartInstance.update();
    }
  }

  loadStats() {
    this.loadingStats = true;
    this.adminService.getStats().subscribe({
      next: (data) => {
        this.stats = data;
        this.loadingStats = false;
        setTimeout(() => this.updateChart(), 0);
      },
      error: (err) => {
        console.error('Error loading stats', err);
        this.loadingStats = false;
      }
    });
  }

  loadUsers() {
    this.loadingUsers = true;
    this.adminService.getUsers().subscribe({
      next: (data) => {
        this.users = data;
        this.loadingUsers = false;
      },
      error: (err) => {
        console.error('Error loading users', err);
        this.loadingUsers = false;
      }
    });
  }

  loadMentors() {
    this.loadingMentors = true;
    this.adminService.getMentors().subscribe({
      next: (data) => {
        this.mentors = data;
        this.loadingMentors = false;
      },
      error: (err) => {
        console.error('Error loading mentors', err);
        this.loadingMentors = false;
      }
    });
  }

  loadRequests() {
    this.loadingRequests = true;
    this.adminService.getPendingMentorRequests().subscribe({
      next: (data) => {
        this.requests = data;
        this.loadingRequests = false;
      },
      error: (err) => {
        console.error('Error loading requests', err);
        this.loadingRequests = false;
      }
    });
  }

  treatRequest(alumniId: number, approve: boolean) {
    this.successMessage = '';
    this.error = '';
    this.adminService.treatMentorRequest(alumniId, approve).subscribe({
      next: () => {
        this.successMessage = approve ? 'Demande approuvée !' : 'Demande refusée.';
        this.requests = this.requests.filter(r => r.id !== alumniId);
        this.loadStats(); 
        this.loadMentors();
      },
      error: (err) => {
        this.error = 'Erreur lors du traitement.';
        console.error(err);
      }
    });
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  getInitials(user: any): string {
    return `${user?.prenom?.[0] || ''}${user?.nom?.[0] || ''}`.toUpperCase() || 'U';
  }

  getUserRoleLabel(user: any): string {
    return user?.typeProfil || user?.role || 'USER';
  }

  removeAccents(str: string): string {
    return str ? str.normalize("NFD").replace(/[\u0300-\u036f]/g, "") : "";
  }

  get filteredUsers() {
    const rawQuery = this.searchQuery.toLowerCase().trim();
    const query = this.removeAccents(rawQuery);
    
    return this.users.filter(user => {
      let matchesSearch = true;
      
      if (query) {
        if (this.searchAttribute === 'all') {
          const allProperties = JSON.stringify(user).toLowerCase();
          matchesSearch = this.removeAccents(allProperties).includes(query);
        } else {
          const attrValue = user[this.searchAttribute];
          matchesSearch = attrValue ? this.removeAccents(String(attrValue).toLowerCase()).includes(query) : false;
        }
      }
      
      const roleLabel = this.getUserRoleLabel(user);
      const matchesRole = this.filterRole ? roleLabel.includes(this.filterRole) : true;
      
      return matchesSearch && matchesRole;
    });
  }
}
