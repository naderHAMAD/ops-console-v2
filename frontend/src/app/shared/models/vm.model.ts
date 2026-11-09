export interface VmModel {
  id: string;
  name: string;
  cpu: number;
  ramGb: number;
  diskGb: number;
  diskType: string;
  template: string;
  environment: string;
  network: string;
  backupEnabled: boolean;
  description: string;
  status: 'pending' | 'active' | 'failed' | 'deleted';
  regionName: string;
  createdAt: string;
}

export interface CreateVmRequest {
  name: string;
  environment: string;
  cpu: number;
  ramGb: number;
  diskGb: number;
  diskType: string;
  template: string;
  network: string;
  backupEnabled: boolean;
  description: string;
  regionId: string;
}