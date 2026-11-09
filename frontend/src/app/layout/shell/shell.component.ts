import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';

@Component({
  selector: 'app-shell',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive, RouterOutlet],
  templateUrl: './shell.component.html'
})
export class ShellComponent {
  constructor(public auth: AuthService) {}

  initials(): string {
    const name = this.auth.currentUser()?.fullName ?? '';
    return name.split(' ').map(p => p[0]).join('').slice(0, 2).toUpperCase();
  }

  logout(): void {
    this.auth.logout();
  }
}
