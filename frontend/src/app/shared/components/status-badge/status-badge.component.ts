import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-status-badge',
  standalone: true,
  imports: [CommonModule],
  template: `
    <span class="badge" [ngClass]="badgeClass()">
      <span class="dot"></span>{{ label }}
    </span>
  `,
  styles: [`
    .badge { display:inline-flex; align-items:center; gap:6px; font-size:11px; padding:4px 9px; border-radius:20px; font-weight:500; }
    .dot { width:6px; height:6px; border-radius:50%; background: currentColor; }
    .ok { background: rgba(45,217,200,0.12); color:#2DD9C8; }
    .warn { background: rgba(245,166,35,0.12); color:#F5A623; }
    .fail { background: rgba(239,91,91,0.12); color:#EF5B5B; }
  `]
})
export class StatusBadgeComponent {
  @Input() status: 'success' | 'running' | 'pending' | 'failed' | 'rolled_back' | 'up' | 'down' = 'pending';
  @Input() label = '';

  badgeClass(): string {
    if (['success', 'up'].includes(this.status)) return 'ok';
    if (['running', 'pending', 'rolled_back'].includes(this.status)) return 'warn';
    return 'fail';
  }
}
