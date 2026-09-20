import { Routes } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';
import { MentorsComponent } from './mentors/mentors.component';
import { MentorDetailsComponent } from './mentors/mentor-details/mentor-details.component';
import { ProfileComponent } from './profile/profile.component';
import { AdminDashboardComponent } from './admin-dashboard/admin-dashboard.component';
import { MentorshipRequestsComponent } from './mentorship-requests/mentorship-requests.component';
import { ChatComponent } from './chat/chat.component';
import { CvUploadComponent } from './cv-upload/cv-upload.component';

export const routes: Routes = [
  { path: '', redirectTo: 'home', pathMatch: 'full' },
  { path: 'home', component: HomeComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'mentors', component: MentorsComponent },
  { path: 'mentors/:id', component: MentorDetailsComponent },
  { path: 'profile', component: ProfileComponent },
  { path: 'cv', component: CvUploadComponent },
  { path: 'mentorship-requests', component: MentorshipRequestsComponent },
  { path: 'conversations', component: ChatComponent },
  { path: 'conversations/:id', component: ChatComponent },
  { path: 'admin/dashboard', component: AdminDashboardComponent },
  { path: '**', redirectTo: 'home' }
];
