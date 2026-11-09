import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../environments/environment';

interface RegionOption { id: string; name: string; }

@Component({
  selector: 'app-vm-create-wizard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './vm-create-wizard.component.html'
})
export class VmCreateWizardComponent implements OnInit {

  step = signal(1);
  regions = signal<RegionOption[]>([]);
  submitting = signal(false);
  jsonGenerated = signal(false);
  generatedSpec = signal<string | null>(null);
  playbookFileName = signal<string | null>(null);
  playbookContent = signal<string | null>(null);
  ansibleOutput = signal<string | null>(null);

  form = {
    name: '',
    environment: 'prod',
    regionId: '',
    cpu: 4,
    ramGb: 8,
    diskGb: 100,
    diskType: 'ssd',
    template: 'ubuntu-22.04-lts',
    network: '',
    backupEnabled: false,
    description: ''
  };

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.http.get<RegionOption[]>(`${environment.apiUrl}/regions`).subscribe(data => this.regions.set(data));
  }

  next(): void { this.step.update(s => Math.min(s + 1, 5)); }
  previous(): void { this.step.update(s => Math.max(s - 1, 1)); }

  regionName(): string {
    return this.regions().find(r => r.id === this.form.regionId)?.name ?? '';
  }

  generateJson(): void {
    const spec = {
      vm_name: this.form.name,
      vm_environment: this.form.environment,
      region: this.regionName(),
      cpu: this.form.cpu,
      ram_gb: this.form.ramGb,
      disk_gb: this.form.diskGb,
      disk_type: this.form.diskType,
      template: this.form.template,
      network: this.form.network,
      backup_enabled: this.form.backupEnabled,
      description: this.form.description
    };
    const json = JSON.stringify(spec, null, 2);
    this.generatedSpec.set(json);
    this.jsonGenerated.set(true);

    const blob = new Blob([json], { type: 'application/json' });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `vm-spec-${this.form.name || 'sans-nom'}.json`;
    a.click();
    window.URL.revokeObjectURL(url);
  }

  onPlaybookSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) return;

    this.playbookFileName.set(file.name);
    const reader = new FileReader();
    reader.onload = () => {
      this.playbookContent.set(reader.result as string);
    };
    reader.readAsText(file);
  }

  deploy(): void {
    if (!this.generatedSpec() || !this.playbookContent()) {
      this.ansibleOutput.set("⚠️ Génère d'abord le JSON et choisis un playbook avant de déployer.");
      return;
    }

    this.submitting.set(true);
    this.ansibleOutput.set(null);

    this.http.post<{ output: string }>(`${environment.apiUrl}/vm/deploy-with-playbook`, {
      playbookContent: this.playbookContent(),
      vmSpecJson: this.generatedSpec()
    }).subscribe({
      next: (res) => {
        this.submitting.set(false);
        this.ansibleOutput.set(res.output);
      },
      error: (err) => {
        this.submitting.set(false);
        this.ansibleOutput.set('Erreur: ' + (err.error?.output ?? err.message));
      }
    });
  }
}