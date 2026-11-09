import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DashboardService } from './dashboard.service';
import { ServerModel } from '../../shared/models/server.model';
import { StatusBadgeComponent } from '../../shared/components/status-badge/status-badge.component';

interface RegionSummary {
  name: string;
  servers: ServerModel[];
  upCount: number;
  percentUp: number;
}

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, StatusBadgeComponent],
  templateUrl: './dashboard.component.html'
})
export class DashboardComponent implements OnInit {

  servers = signal<ServerModel[]>([]);
  recentAudit = signal<any[]>([]);

  regionSummaries = computed<RegionSummary[]>(() => {
    const groups = new Map<string, ServerModel[]>();
    for (const s of this.servers()) {
      const list = groups.get(s.regionName) ?? [];
      list.push(s);
      groups.set(s.regionName, list);
    }
    return Array.from(groups.entries()).map(([name, list]) => {
      const upCount = list.filter(s => s.status === 'up').length;
      return {
        name,
        servers: list,
        upCount,
        percentUp: list.length ? Math.round((upCount / list.length) * 100) : 0
      };
    });
  });

  constructor(private dashboardService: DashboardService) {}

  ngOnInit(): void {
    this.dashboardService.getServers().subscribe(data => this.servers.set(data));
    this.dashboardService.getRecentAudit().subscribe({
      next: data => this.recentAudit.set(data.slice(0, 8)),
      error: () => this.recentAudit.set([]) // pas de permission : on affiche juste rien
    });
  }
}
