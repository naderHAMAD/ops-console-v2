import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UpdateJobService } from './update-trigger.service';
import { ServerModel } from '../../../shared/models/server.model';
import { StatusBadgeComponent } from '../../../shared/components/status-badge/status-badge.component';

interface UpdateResult {
  jobId: string;
  hostname: string;
  status: 'success' | 'failed' | 'rolled_back' | 'running';
  logs: string;
}

@Component({
  selector: 'app-update-trigger',
  standalone: true,
  imports: [CommonModule, FormsModule, StatusBadgeComponent],
  templateUrl: './update-trigger.component.html'
})
export class UpdateTriggerComponent implements OnInit {

  servers = signal<ServerModel[]>([]);
  selectedIds = signal<Set<string>>(new Set());
  targetVersion = signal('7.4.0');
  launching = signal(false);
  results = signal<UpdateResult[]>([]);

  constructor(private updateService: UpdateJobService) {}

  ngOnInit(): void {
    this.updateService.getServers().subscribe(data => this.servers.set(data));
  }

  toggleSelection(id: string): void {
    const current = new Set(this.selectedIds());
    current.has(id) ? current.delete(id) : current.add(id);
    this.selectedIds.set(current);
  }

  isSelected(id: string): boolean {
    return this.selectedIds().has(id);
  }

  launchUpdate(): void {
    if (this.selectedIds().size === 0) return;
    this.launching.set(true);
    this.results.set([]);

    this.updateService.triggerUpdate({
      serverIds: Array.from(this.selectedIds()),
      targetVersion: this.targetVersion()
    }).subscribe({
      next: (res: any) => {
        this.launching.set(false);
        this.results.set(res);
        this.updateService.getServers().subscribe(data => this.servers.set(data));
      },
      error: (err) => {
        this.launching.set(false);
        this.results.set([{ jobId: '-', hostname: '-', status: 'failed', logs: 'Erreur: ' + err.message }]);
      }
    });
  }

  launchRollback(specificServerId?: string): void {
    let ids: string[];
    if (specificServerId) {
      ids = [specificServerId];
    } else {
      if (this.selectedIds().size === 0) return;
      ids = Array.from(this.selectedIds());
    }

    this.launching.set(true);
    this.results.set([]);

    this.updateService.triggerRollback({
      serverIds: ids
    }).subscribe({
      next: (res: any) => {
        this.launching.set(false);
        this.results.set(res);
        this.updateService.getServers().subscribe(data => this.servers.set(data));
      },
      error: (err) => {
        this.launching.set(false);
        this.results.set([{ jobId: '-', hostname: '-', status: 'failed', logs: 'Erreur Rollback: ' + err.message }]);
      }
    });
  }

  getServerIdByHostname(hostname: string): string | undefined {
    return this.servers().find(s => s.hostname === hostname)?.id;
  }
}