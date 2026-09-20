import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  credentials = {
    email: '',
    motDePasse: ''
  };
  rememberMe = false;
  error: string | null = null;

  constructor(private authService: AuthService, private router: Router) {}

  onSubmit() {
    this.error = null;
    this.authService.login(this.credentials).subscribe({
      next: (response: any) => {
        console.log('Login successful', response);
        this.authService.saveToken(response.token);
        this.authService.saveUserInfo(response);
        
        // Redirect based on role
        if (response.role === 'ROLE_ADMIN' || response.role === 'ADMIN') {
          this.router.navigate(['/admin/dashboard']);
        } else {
          this.router.navigate(['/home']);
        }
      },
      error: (err: any) => {
        console.error('Login failed', err);
        this.error = err.error?.message || 'Invalid email or password';
      }
    });
  }
}
