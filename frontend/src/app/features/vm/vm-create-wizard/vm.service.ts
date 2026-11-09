import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../environments/environment';
import { CreateVmRequest, VmModel } from '../../../shared/models/vm.model';

@Injectable({ providedIn: 'root' })
export class VmService {
  constructor(private http: HttpClient) {}

  listVms() {
    return this.http.get<VmModel[]>(`${environment.apiUrl}/vm`);
  }

  createVm(payload: CreateVmRequest) {
    return this.http.post<{ jobId: string }>(`${environment.apiUrl}/vm`, payload);
  }
}
